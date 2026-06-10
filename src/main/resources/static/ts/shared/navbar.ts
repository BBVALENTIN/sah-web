function navbarQuickPlay() {
    const quickPlayNav = document.getElementById('quickPlayNav');

    if(quickPlayNav) {
        quickPlayNav.addEventListener('click', async () => {
            const res = await fetch('/api/lobby/createQuick');
            window.location.href = await res.text();
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