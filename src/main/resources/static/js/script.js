// to make this script in typescript..
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
    if(quickPlayNav)
    {
        quickPlayNav.addEventListener('click', async () => {
            // let availableLobbies = [];
            // const responseAvailableLobbies = await fetch(`api/lobbies/{AVAILABLE}`);
            const responsePlay = await fetch('/api/play/createQuick');
            if(!responsePlay.ok) {
                console.error('API error');
                return;
            }
            let lobby = await responsePlay.json();
            window.location.href =`/play=${lobby.lobbyId}`;
        });
    }


    const yourProfile = document.getElementById('user-profile');
    const yourProfileNav = document.getElementById('user-profile-nav');
    const yourProfileAvatar = document.getElementById('user-avatar-mini');

    if(yourProfileNav)
    {
        yourProfileNav.innerText = loggedUser;
    }
    if(yourProfile)
    {
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
    }

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


    async function toggleLike(btn, endpoint) {
        const id = btn.dataset.commentId || btn.dataset.postId;
        const countEl = btn.querySelector('.like-count');

        await fetch(`/api/community/${endpoint}/${id}`, { method: 'POST' });

        const isLiked = btn.classList.toggle('liked');
        countEl.textContent = isLiked
            ? parseInt(countEl.textContent) + 1
            : parseInt(countEl.textContent) - 1;
    }

    document.querySelectorAll('.like-comments').forEach(btn => {
        btn.addEventListener('click', (e) => {
            console.log(btn);
            toggleLike(btn, 'likeComment');
        });
    });

    document.querySelectorAll('.like-button').forEach(btn => {
        btn.addEventListener('click', (e) => {
            console.log(btn);
            toggleLike(btn, 'likePost');
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

    document.querySelectorAll('.post-item').forEach(item => {
        item.addEventListener('click', (e) => {
            if(e.target.closest('button')) return;

            const postId = item.dataset.postId;
            window.location.href = `/post/${postId}`;
        })
    });

    const btnSubmitComment = document.getElementById('submit-comment-button');
    if(btnSubmitComment) {
        btnSubmitComment.addEventListener('click', async () => {
            const input = document.getElementById('comment-input');
            let inputContent = "";
            if(input instanceof HTMLTextAreaElement)
            {
                inputContent = input.value;
            }
            const postId = btnSubmitComment.dataset.postId;
            console.log(postId);
            await fetch('/api/community/comment', {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    postId: postId,
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

            input.value = '';
            input.style.height = 'auto';
            document.getElementById('char-count').textContent = '';
            btnSubmitComment.disabled = true;
        });
    }

    const settingsBtn = document.querySelector('.settings-btn');
    if(settingsBtn) {
        settingsBtn.addEventListener('click', () => {
            window.location.href = '/settings/user';
        });
    }

    document.querySelectorAll('.delete-button').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.stopPropagation();
            const postId = btn.dataset.postId;
            const commentId = btn.dataset.commentId;
            if(postId) {
                fetch(`/api/community/deletePost/${postId}`, {method: 'POST'});
                window.location.href='/community';
            }
            else {
                fetch(`/api/community/deleteComment/${commentId}`, {method: 'POST'});
                window.location.reload()
            }
        });
    });

    const editDescBtn = document.getElementById('edit-description-button');
    if(editDescBtn) {
        editDescBtn.addEventListener('click', handleEdit);
    }

    function handleEdit() {
        const descDiv = document.querySelector('.profile-description');
        const currentText = descDiv.textContent;
        descDiv.innerHTML = `<input type="text" id="desc-input" class="desc-input" value="${currentText}" maxlength="50"/>`;

        const input = document.getElementById('desc-input');
        input.focus();
        editDescBtn.innerHTML = '<i class="ti ti-check"></i>';
        editDescBtn.removeEventListener('click', handleEdit);
        editDescBtn.addEventListener('click', handleSave);
    }

    async function handleSave() {
        const input = document.getElementById('desc-input');
        const descDiv = document.querySelector('.profile-description');
        const newDesc = input.value.trim();

        await fetch('/api/settings/changeDescription', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ description: newDesc })
        });

        descDiv.textContent = newDesc;
        editDescBtn.innerHTML = '<i class="ti ti-pencil"></i>';
        editDescBtn.removeEventListener('click', handleSave);
        editDescBtn.addEventListener('click', handleEdit);
    }

    function addSelectCountryComponent(parentComponentId, selectedValue = '') {
        if(!parentComponentId) return;

        const countries = [
            { code: 'AF', name: 'Afghanistan'},
            { code: 'RO', name: 'Romania'},
            { code: 'US', name: 'United States'}
        ];
        const selectCountry = document.createElement('select');
        selectCountry.id = "select-country";
        selectCountry.name = 'country';
        selectCountry.autocomplete = 'country';

        countries.forEach(c => {
            const option = document.createElement('option');
            option.value = c.code;
            option.textContent = c.name;
            if(c.code === selectedValue) option.selected = true;
            selectCountry.appendChild(option);
        });

        document.getElementById(parentComponentId).appendChild(selectCountry);
    }

    addSelectCountryComponent('country-select-container', 'RO');
});