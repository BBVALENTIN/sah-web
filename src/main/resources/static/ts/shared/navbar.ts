function navbarQuickPlay() {
    const quickPlayNav = document.getElementById('quickPlayNav');

    if(quickPlayNav) {
        quickPlayNav.addEventListener('click', async () => {
            const responsePlay = await fetch('/api/play/createQuick');
            if(!responsePlay.ok) {
                console.error('API error');
                return;
            }
            let lobby = await responsePlay.json();
            window.location.href =`/play=${lobby.lobbyId}`;
        });
    }
    else {
        console.log("There is no navbar in your page");
    }
}

export function navbarFuncs() {
    navbarQuickPlay();
}