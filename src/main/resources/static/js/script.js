addEventListener('DOMContentLoaded', () => {
    const quickPlayNav = document.getElementById('quickPlayNav');

    quickPlayNav.addEventListener('click', async () => {
        const responsePlay = await fetch('/api/play/createQuick', {
            method: 'POST'
        });
        if(!responsePlay.ok) {
            console.error('API error');
            return;
        }
        let lobby = await responsePlay.json();
        window.location.href =`play=${lobby.lobbyId}`;
    });
});