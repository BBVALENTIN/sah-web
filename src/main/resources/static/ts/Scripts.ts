import { LobbyType} from "./Enums.js";
import {Lobby} from "./Types";

document.addEventListener(("DOMContentLoaded"), () => {
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