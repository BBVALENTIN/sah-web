import {getInfoUser} from '../ts-transpiled/APIs.js';

addEventListener('DOMContentLoaded', async () => {
    const quickPlayNav = document.getElementById('quickPlayNav');
    const lobbyInfo = await getInfoUser();
    quickPlayNav.addEventListener('click', async () => {
        const responsePlay = await fetch('/api/play/createQuick', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(lobbyInfo.loggedUsername.username)
        });
        if(!responsePlay.ok) {
            console.error('API error');
            return;
        }
        let lobby = await responsePlay.json();
        window.location.href =`play=${lobby.lobbyId}`;
    });
});