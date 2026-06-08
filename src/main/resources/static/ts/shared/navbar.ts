function navbarQuickPlay() {
    const quickPlayNav = document.getElementById('quickPlayNav');

    if(quickPlayNav) {
        quickPlayNav.addEventListener('click', async () => {
            const responsePlay = await fetch('/api/lobby/createQuick');
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

function redirectables() {
    document.querySelectorAll('.redirectable').forEach(element => {
        element.addEventListener('click', () => {
            const username = (element as HTMLElement).dataset.username;

            window.location.href = `/profile/${username}`;
        });
    });
}

export function navbarFuncs() {
    navbarQuickPlay();
    redirectables();
}