import type {lobbyInfo} from "./Types.js";

export let loggedUsername: string | undefined = undefined;
export const globalLobbyInfo = await getInfoLobby();
export const initUser = async () => {
    const response = await fetch('/info/user');
    if(response.ok) {
        const data = await response.json();
        loggedUsername = data.username;
        return loggedUsername;
    }
    return undefined
};

initUser().then(name => console.log("Username: ", name));

export async function getAllPGN() {
    const responsePGN = await fetch('/api/chess/PGN_ALL');
    if(responsePGN.ok) {
        const PGN= await responsePGN.text();
        return PGN;
    }
    console.error("Nu s-au citit date din API PGN");
    return undefined;
}

export async function getInfoLobby(): Promise<lobbyInfo | undefined> {
    const response = await fetch('/info/lobby');
    if(response.ok) {
        return await response.json();
    }
    return undefined;
}