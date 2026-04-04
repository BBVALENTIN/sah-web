addEventListener('DOMContentLoaded', async () => {
    const quickPlayNav = document.getElementById('quickPlayNav');
    let loggedUser = null;

    async function fetchUserInfo() {
        const responseUserInfo = await fetch('/info/user');
        let userInfoJSON = null;
        if(responseUserInfo.ok) {
            userInfoJSON = await responseUserInfo.json();
            loggedUser = userInfoJSON.username;
        }
    }

    await fetchUserInfo();
    console.log(loggedUser);
    quickPlayNav.addEventListener('click', async () => {
        // let availableLobbies = [];
        // const responseAvailableLobbies = await fetch(`api/lobbies/{AVAILABLE}`);
        console.log("user logat: ", loggedUser);
        const responsePlay = await fetch('/api/play/createQuick', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: loggedUser
        });
        if(!responsePlay.ok) {
            console.error('API error');
            return;
        }
        let lobby = await responsePlay.json();
        window.location.href =`play=${lobby.lobbyId}`;
    });

    const yourProfile = document.getElementById('user-profile');
    yourProfile.addEventListener('click', async () => {
        try {
            const response = await fetch(`/profile/${loggedUser}`);
            if(response.ok) {
                window.location.href = `/profile/${loggedUser}`;
            }
        } catch (e) {
           console.log(e);
        }
    });
});