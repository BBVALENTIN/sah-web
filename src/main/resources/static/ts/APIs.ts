import type {lobbyInfo, userInfo} from "./Types.js";

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

export async function getInfoLobby(): Promise<lobbyInfo | undefined> {
    const response = await fetch('/info/lobby');
    if(response.ok) {
        return await response.json();
    }
    return undefined;
}