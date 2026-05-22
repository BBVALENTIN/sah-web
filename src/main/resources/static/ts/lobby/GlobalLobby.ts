import {lobbyInfo} from "../Types.js";
import {LobbyType} from "../Enums.js";

declare var SockJS: any;
declare var Stomp: any;

let currentUsername: string = "Guest";

const state = {
    stompClient: null as any,
    connected: false
};

document.addEventListener("DOMContentLoaded", () => {
    loadLobbies();
    conectareWebSocket();
});

function loadLobbies() {
    fetch(`/api/lobbies/${LobbyType.AVAILABLE}`)
        .then(res => res.json())
        .then((lobbies: lobbyInfo[]) => {
            lobbies.forEach(lobby => adaugaLobbyInTabel(lobby));
        })
        .catch(err => console.error("Eroare la încărcarea inițială:", err));
}

function conectareWebSocket() {
    const socket = new SockJS('/ws');
    state.stompClient = Stomp.over(socket);

    state.stompClient.connect({}, function () {
        console.log("Conectat la fluxul global de lobby-uri");

        state.stompClient.subscribe('/topic/global-lobbies', function (payload: any) {
            const lobbyDto: lobbyInfo = JSON.parse(payload.body);

            if (lobbyDto.lobbyType === LobbyType.AVAILABLE) {
                adaugaLobbyInTabel(lobbyDto);
            }
            else {
                stergeLobbyDinTabel(lobbyDto.lobbyId);
            }
        });
    }, function(error: any) {
        console.error("STOMP ERROR GLOBAL: ", error);
    });
}

function adaugaLobbyInTabel(lobby: lobbyInfo) {
    let tableBody = document.getElementById("lobby-table-body") as HTMLTableSectionElement;

    if (!tableBody) {
        const container = document.getElementById("lobbiesContainer");
        if (container) {
            container.innerHTML = `
                <table class="lobby-table" style="width: 100%; border-collapse: collapse; margin-top: 20px;">
                    <thead>
                        <tr style="background-color: #080710; text-align: left;">
                            <th style="padding: 10px; border-bottom: 1px solid #080710;">Creator</th>
                            <th style="padding: 10px; border-bottom: 1px solid #080710">Lobby Id</th>
                            <th style="padding: 10px; border-bottom: 1px solid #080710;">Status</th>
                            <th style="padding: 10px; border-bottom: 1px solid #080710;">Action</th>
                        </tr>
                    </thead>
                    <tbody id="lobby-table-body"></tbody>
                </table>
            `;
            tableBody = document.getElementById("lobby-table-body") as HTMLTableSectionElement;
        }
    }

    if (!tableBody) {
        console.error("Nu s-a găsit containerul #lobbiesContainer în HTML!");
        return;
    }

    let randExistent = document.getElementById(`lobby-${lobby.lobbyId}`);
    const creator = lobby.playerWhite ? lobby.playerWhite : (lobby.playerBlack ? lobby.playerBlack : "Unknown");

    if (randExistent) {
        randExistent.innerHTML = `
            <td style="padding: 10px; border-bottom: 1px solid #080710;">${creator}</td>
            <td style="padding: 10px; border-bottom: 1px solid #080710;">${lobby.lobbyId}</td>
            <td style="padding: 10px; border-bottom: 1px solid #080710;">Waiting for opponent...</td>
            <td style="padding: 10px; border-bottom: 1px solid #080710;"><button class="join-btn" data-id="${lobby.lobbyId}">Join</button></td>
        `;
    } else {
        const tr = document.createElement("tr");
        tr.id = `lobby-${lobby.lobbyId}`;
        tr.innerHTML = `
            <td style="padding: 10px; border-bottom: 1px solid #080710;">${creator}</td>
            <td style="padding: 10px; border-bottom: 1px solid #080710;">${lobby.lobbyId}</td>
            <td style="padding: 10px; border-bottom: 1px solid #080710;">Open</td>
            <td style="padding: 10px; border-bottom: 1px solid #080710;"><button class="join-btn" data-id="${lobby.lobbyId}">Join</button></td>
        `;
        tableBody.appendChild(tr);
    }

    atasazaEvenimentJoin();
}

function stergeLobbyDinTabel(lobbyId: string) {
    const row = document.getElementById(`lobby-${lobbyId}`);
    if (row) {
        row.remove();
    }
}

function atasazaEvenimentJoin() {
    const butoane = document.querySelectorAll(".join-btn");
    butoane.forEach(buton => {
        buton.removeEventListener("click", gestionareJoin);
        buton.addEventListener("click", gestionareJoin);
    });
}

function gestionareJoin(event: any) {
    const lobbyId = event.target.getAttribute("data-id");

    fetch(`/api/joinLobby/${lobbyId}`, {
        method: 'POST',
    })
        .then(res => res.text())
        .then(data => {
            if (data === "lobbyFull") {
                alert("Ne pare rău, masa s-a ocupat între timp!");
            } else if (data.startsWith("play=")) {
                window.location.href = "/" + data;
            }
        });
}