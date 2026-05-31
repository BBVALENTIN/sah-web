addEventListener('DOMContentLoaded', async () => {
    const quickPlayNav = document.getElementById('quickPlayNav');
    let loggedUser = null;
    let avatar = null;

    async function fetchUserInfo() {
        const responseUserInfo = await fetch('/info/user');
        let userInfoJSON = null;
        if(responseUserInfo.ok) {
            userInfoJSON = await responseUserInfo.json();
            loggedUser = userInfoJSON.username;
            avatar = userInfoJSON.avatar;
        }
    }

    await fetchUserInfo();
    console.log(loggedUser, " ", avatar);
    quickPlayNav.addEventListener('click', async () => {
        // let availableLobbies = [];
        // const responseAvailableLobbies = await fetch(`api/lobbies/{AVAILABLE}`);
        console.log("user logat: ", loggedUser);
        const responsePlay = await fetch('/api/play/createQuick');
        if(!responsePlay.ok) {
            console.error('API error');
            return;
        }
        let lobby = await responsePlay.json();
        window.location.href =`/play=${lobby.lobbyId}`;
    });

    const yourProfile = document.getElementById('user-profile');
    const yourProfileNav = document.getElementById('user-profile-nav');
    const yourProfileAvatar = document.getElementById('user-avatar-mini');

    yourProfileNav.innerText = loggedUser;
    // yourProfileAvatar.src = `/uploads/${avatar}`;
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