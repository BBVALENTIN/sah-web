
export async function communityFuncs() {
    const info: LoggedUser = await fetchUserInfo();
    handlePostNavigation();
    handlePost(info);
    handleComment(info);
    handleDelete();
    setupLikeButtons();
}

interface LoggedUser {
    username: string;
    avatar: string;
}

async function fetchUserInfo(): Promise<LoggedUser> {
    const responseUserInfo = await fetch('/info/user');
    let userInfoJSON = null;
    if(responseUserInfo.ok) {
        userInfoJSON = await responseUserInfo.json();
        return {
            username: userInfoJSON.username,
            avatar: userInfoJSON.avatar
        };
    }
    return { username: '', avatar: '' };
}

function setupTextArea(textAreaId: string, buttonId: string): HTMLTextAreaElement | null {
    const textArea = document.getElementById(textAreaId) as HTMLTextAreaElement;
    const button = document.getElementById(buttonId) as HTMLButtonElement;
    if (!textArea || !button) return null;

    button.disabled = true;

    textArea.addEventListener('input', () => {
        textAreaStyle(textArea);

        const counter = document.getElementById('char-count') as HTMLParagraphElement;
        if (counter) counter.textContent = `${textArea.value.length}/${textArea.maxLength}`;

        button.disabled = textArea.value.trim().length === 0;
    });

    return textArea;
}

function textAreaStyle(html: HTMLTextAreaElement) {
    html.style.height = 'auto';
    html.style.height = html.scrollHeight + 'px';
}

async function toggleLike(btn: HTMLButtonElement, endpoint: string): Promise<void> {
    const id = btn.dataset.commentId || btn.dataset.postId;
    const countEl = btn.querySelector('.like-count') as HTMLSpanElement;
    if (!id || !countEl) return;

    await fetch(`/api/community/${endpoint}/${id}`, { method: 'POST' });

    const isLiked = btn.classList.toggle('liked');
    countEl.textContent = String(isLiked
        ? parseInt(countEl.textContent || '0') + 1
        : parseInt(countEl.textContent || '0') - 1);
}

function handlePostNavigation() {
    document.querySelectorAll<HTMLElement>('.post-item').forEach(item => {
       item.addEventListener('click', (e) => {
          if((e.target as HTMLElement).closest('button')) return;
          const postId = item.dataset.postId;
          if(postId) window.location.href= `/post/${postId}`;
       });
    });
}

function setupLikeButtons(): void {
    document.querySelectorAll<HTMLButtonElement>('.like-comments').forEach(btn => {
        btn.addEventListener('click', () => toggleLike(btn, 'likeComment'));
    });

    document.querySelectorAll<HTMLButtonElement>('.like-button').forEach(btn => {
        btn.addEventListener('click', () => toggleLike(btn, 'likePost'));
    });

    const selectedPostLike = document.getElementById('selected-post-like-button') as HTMLButtonElement;
    if (selectedPostLike) {
        selectedPostLike.addEventListener('click', () => toggleLike(selectedPostLike, 'likePost'));
    }
}

function handlePost(user: LoggedUser): void {
    const textArea = setupTextArea('post-input', 'submit-post-button');
    if (!textArea) return;

    const button = document.getElementById('submit-post-button') as HTMLButtonElement;
    button.addEventListener('click', async () => {
        const content = textArea.value.trim();
        if (!content) return;

        await fetch('/api/community/post', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ content })
        });

        createTemporaryPost(textArea, content, user);
    });
}

function handleComment(user: LoggedUser): void {
    const textArea = setupTextArea('comment-input', 'submit-comment-button');
    if (!textArea) return;

    const button = document.getElementById('submit-comment-button') as HTMLButtonElement;
    button.addEventListener('click', async () => {
        const content = textArea.value.trim();
        if (!content) return;

        const postId = button.dataset.postId;
        await fetch('/api/community/comment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ postId, content })
        });

        createTemporaryPost(textArea, content, user);
    });
}

function createTemporaryPost(textArea: HTMLTextAreaElement, content: string, user:LoggedUser) {

    const postsContainer = document.querySelector('.posts-container');
    if(!postsContainer) return;
    const newPost = document.createElement('div');
    newPost.classList.add('post-item');
    newPost.innerHTML = `
                    <img class="mini-profile-picture" src="/uploads/${user.avatar}" alt="avatar"/>
                    <div class="post-item-content">
                        <div class="post-item-header">
                            <span class="post-username">${user.username}</span>
                            <span class="post-time">acum</span>
                        </div>
                        <p class="post-text">${content}</p>
                    </div>`;

    postsContainer.prepend(newPost);

    textArea.value = '';
    textArea.style.height = 'auto';
    const countEl = document.getElementById('char-count') as HTMLParagraphElement;
    const submitBtn = document.getElementById('submit-post-button') as HTMLButtonElement;

    if(!countEl || !submitBtn) return;
    countEl.textContent = '';
    submitBtn.disabled = true;
}

function handleDelete() {
    document.querySelectorAll<HTMLButtonElement>('.delete-button').forEach(btn => {
       btn.addEventListener('click', (e) => {
           e.stopPropagation();
           const postId = btn.dataset.postId;
           const commentId = btn.dataset.commentId;

           if (postId) {
               fetch(`/api/community/deletePost/${postId}`, { method: 'POST' })
                   .then(() => btn.closest('.post-item')?.remove());
           } else if (commentId) {
               fetch(`/api/community/deleteComment/${commentId}`, { method: 'POST' })
                   .then(() => btn.closest('.post-item')?.remove());
           }
       });
    });
}