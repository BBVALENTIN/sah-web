import {lobbyInfo} from "../tools/Types.js";
import {LobbyType} from "../tools/Enums.js";

declare var SockJS: any;
declare var Stomp: any;

const state = {
    stompClient: null as any,
    connected: false
};
let lobbyCount = 0;
const lobbyInfo = document.getElementById('lobbiesCount') as HTMLDivElement;
document.addEventListener("DOMContentLoaded", () => {
    loadLobbies();
    connectWS();
    handleCreate();
});

function loadLobbies() {
    fetch(`/api/lobby/${LobbyType.AVAILABLE}`)
        .then(res => res.json())
        .then((lobbies: lobbyInfo[]) => {
            lobbies.forEach(lobby => addLobbyRow(lobby));
        })
        .catch(err => console.error("Loading error:", err));
}

function connectWS() {
    const socket = new SockJS('/ws');
    state.stompClient = Stomp.over(socket);

    state.stompClient.connect({}, function () {
        console.log("Connected to the lobby stream");

        state.stompClient.subscribe('/topic/global-lobbies', function (payload: any) {
            const lobbyDto: lobbyInfo = JSON.parse(payload.body);

            if (lobbyDto.lobbyType === LobbyType.AVAILABLE) {
                lobbyCount++;
                addLobbyRow(lobbyDto);
            }
            else {
                deleteTableRow(lobbyDto.lobbyId);
            }
            if(lobbyCount === 0) {
                lobbyInfo.innerText = "There are currently no lobbies open";
            }
            else if(lobbyCount === 1){
                lobbyInfo.innerText = "There is one lobby open";
            }
            else {
                lobbyInfo.innerText = `There are currently ${lobbyCount} lobbies opened`;
            }
        });
    }, function(error: any) {
        console.error("STOMP ERROR GLOBAL: ", error);
    });
}

function addLobbyRow(lobby: lobbyInfo) {
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
        console.error("There's no lobby container.");
        return;
    }

    let createdRow = document.getElementById(`lobby-${lobby.lobbyId}`);
    const creator = lobby.playerWhite ? lobby.playerWhite : (lobby.playerBlack ? lobby.playerBlack : "Unknown");

    if (createdRow) {
        createdRow.innerHTML = `
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
        lobbyCount++;
        tableBody.appendChild(tr);
    }

    getJoinEvent();
}

function deleteTableRow(lobbyId: string) {
    const row = document.getElementById(`lobby-${lobbyId}`);
    if (row) {
        lobbyCount--;
        row.remove();
    }
}

function getJoinEvent() {
    const joinButtons = document.querySelectorAll(".join-btn");
    joinButtons.forEach(buton => {
        buton.removeEventListener("click", handleJoin);
        buton.addEventListener("click", handleJoin);
    });
}

function handleJoin(event: any) {
    const lobbyId = event.target.getAttribute("data-id");

    fetch(`/api/lobby/${lobbyId}`, {
        method: 'POST',
    })
        .then(res => res.text())
        .then(data => {
            if (data === "lobbyFull") {
                alert("The lobby is already full.");
            } else if (data.startsWith("play=")) {
                window.location.href = "/" + data;
            }
        });
}

function handleCreate() {
    const createButton = document.getElementById('createLobbyButton');
    createButton?.addEventListener('click', async () => {
        const res = await fetch('/api/lobby/create');
        window.location.href = await res.text();
    })
}