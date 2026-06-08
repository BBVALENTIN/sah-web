export function profileFuncs() {
    openSettings();
    setupEditDescription()
}

function openSettings() {
    const settingsButton= document.querySelector('.settings-btn');
    if(!settingsButton) return;

    settingsButton.addEventListener('click', () => {
       window.location.href = '/settings/user';
    });
}

function setupEditDescription(): void {
    const editDescBtn = document.getElementById('edit-description-button') as HTMLButtonElement;
    if (!editDescBtn) return;

    function handleEdit(): void {
        const descDiv = document.querySelector('.profile-description') as HTMLElement;
        if (!descDiv) return;

        descDiv.innerHTML = `<input type="text" id="desc-input" class="desc-input" value="${descDiv.textContent}" maxlength="50"/>`;

        const input = document.getElementById('desc-input') as HTMLInputElement;
        if (!input) return;
        input.focus();

        editDescBtn.innerHTML = '<i class="ti ti-check"></i>';
        editDescBtn.removeEventListener('click', handleEdit);
        editDescBtn.addEventListener('click', handleSave);
    }

    async function handleSave(): Promise<void> {
        const input = document.getElementById('desc-input') as HTMLInputElement;
        const descDiv = document.querySelector('.profile-description') as HTMLElement;
        if (!input || !descDiv) return;

        const newDesc = input.value.trim();

        await fetch('/api/settings/changeDescription', {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({description: newDesc })
        });

        descDiv.textContent = newDesc;
        editDescBtn.innerHTML = '<i class="ti ti-pencil"></i>';
        editDescBtn.removeEventListener('click', handleSave);
        editDescBtn.addEventListener('click', handleEdit);
    }

    editDescBtn.addEventListener('click', handleEdit);
}

