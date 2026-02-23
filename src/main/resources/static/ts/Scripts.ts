import { LobbyType} from "./Enums.js";
import {Lobby} from "./Types.js";
import {loggedUsername} from "./APIs.js";

interface lobbyDTO {
    lobbyId: string;
    lobbyType: LobbyType;
}
document.addEventListener(("DOMContentLoaded"), () => {

    const showAvailableLobbiesButton = document.getElementById('showAvailableLobbiesButton') as HTMLButtonElement;
    const lobbiesContainer = document.getElementById('lobbiesContainer') as HTMLElement;
    showAvailableLobbies();

    async function showAvailableLobbies():Promise<void> {
        let lobbies = null;
        const responseAvailableLobbies = await fetch(`/api/lobbies/${LobbyType.AVAILABLE}`);

        if(responseAvailableLobbies.ok) {
            lobbies = await responseAvailableLobbies.json();
        }
        renderLobbyList(lobbies);
    }

    showAvailableLobbiesButton.addEventListener('click', () => {
        showAvailableLobbies();
    });

    const createLobbyButton = document.getElementById("createLobbyButton") as HTMLButtonElement;

    createLobbyButton.addEventListener(("click"), async () => {
        try {
            const response = await fetch("/api/play/create", {
                method: 'POST',
                headers: {
                    'Content-Type': "application/json"
                },
                body: JSON.stringify({type: LobbyType.AVAILABLE, username: loggedUsername})
            });
            if (!response.ok) {
                console.error("Nu s-a putut crea lobby");
                return;
            }

            const lobby: Lobby = await response.json();
            console.log(lobby);

            window.location.href = `play=${lobby.lobbyId}`
        }
        catch(e){
            console.log(e);
        }
    });

    function renderLobbyList(lobbies: lobbyDTO[]) {
        lobbiesContainer.innerHTML = '';

        if(lobbies.length === 0) {
            lobbiesContainer.innerHTML = '<p>No available lobbies, you can be the first to create one!</p>';
            return;
        }
        lobbies.forEach(lobby => {
            const lobbyDiv = document.createElement('div');
            lobbyDiv.classList.add('lobby-item');
            lobbyDiv.textContent = `Lobby ID: ${lobby.lobbyId} | Type: ${lobby.lobbyType}`;
            lobbyDiv.addEventListener('click', () => {
               window.location.href = `play=${lobby.lobbyId}`;
            });
            lobbiesContainer.appendChild(lobbyDiv);
        });
    }
});