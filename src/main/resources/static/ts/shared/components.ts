function addSelectCountryComponent(parentComponent: HTMLDivElement, selectedValue = '') {
    if(!parentComponent) return;

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

    parentComponent.appendChild(parentComponent);
}