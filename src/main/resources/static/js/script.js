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

    const textArea = document.getElementById('post-input');
    if(textArea) {
        textArea.addEventListener('input', () => {
            textArea.style.height = 'auto';
            textArea.style.height = textArea.scrollHeight + 'px';

            const counter = document.getElementById('char-count');
            const postButton = document.getElementById('submit-post-button');

            counter.textContent = `${textArea.value.length}/${textArea.maxLength}`;
            postButton.disabled = textArea.value.length === 0;
        });

        const postButton = document.getElementById('submit-post-button');
        if(postButton) postButton.disabled = true;
    }

    document.querySelectorAll('.like-button').forEach(btn => {
        btn.addEventListener('click', async () => {
            const postId = btn.dataset.postId;
            const countEl = btn.querySelector('.like-count');

            await fetch(`/api/community/like/${postId}`, { method: 'POST' });

            const isLiked = btn.classList.toggle('liked');
            countEl.textContent = isLiked
                ? parseInt(countEl.textContent) + 1
                : parseInt(countEl.textContent) - 1;
        });
    });

    const buttonSubmit = document.getElementById('submit-post-button');
    if(buttonSubmit) {
        buttonSubmit.addEventListener('click', async () => {
            const input = document.getElementById('post-input');
            let inputContent = "";
            if(input instanceof HTMLTextAreaElement)
            {
                inputContent = input.value;
            }
            await fetch('/api/community/post', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    content: inputContent
                })
            });

            const postsContainer = document.querySelector('.posts-container');
            const newPost = document.createElement('div');
            newPost.classList.add('post-item');
            newPost.innerHTML = `
                    <img class="mini-profile-picture" src="/uploads/${avatar}" alt="avatar"/>
                    <div class="post-item-content">
                        <div class="post-item-header">
                            <span class="post-username">${loggedUser}</span>
                            <span class="post-time">acum</span>
                        </div>
                        <p class="post-text">${inputContent}</p>
                    </div>`;

            postsContainer.prepend(newPost);

            textArea.value = '';
            textArea.style.height = 'auto';
            document.getElementById('char-count').textContent = '';
            document.getElementById('submit-post-button').disabled = true;
        });
    }
});