import { LobbyType} from "./Enums.js";
import {Lobby} from "./Types.js";

interface lobbyDTO {
    lobbyId: string;
    lobbyType: LobbyType;
}

document.addEventListener(("DOMContentLoaded"), () => {

    const showAvailableLobbiesButton = document.getElementById('showAvailableLobbiesButton') as HTMLButtonElement;
    showAvailableLobbies();

    async function showAvailableLobbies():Promise<lobbyDTO[]> {
        let lobbies = null;
        const responseAvailableLobbies = await fetch(`/api/lobbies/${LobbyType.AVAILABLE}`);

        if(responseAvailableLobbies.ok) {
            lobbies = await responseAvailableLobbies.json();
        }
        console.log(lobbies);
        return lobbies;
    }

    const createLobbyButton = document.getElementById("createLobbyButton") as HTMLButtonElement;

    createLobbyButton.addEventListener(("click"), async () => {
        try {
            const response = await fetch("/api/play/create", {
                method: 'POST',
                headers: {
                    'Content-Type': "application/json"
                },
                body: JSON.stringify(LobbyType.AVAILABLE)
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
});