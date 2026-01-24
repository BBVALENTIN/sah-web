export class Grid {
    constructor(public rows: number, public col: number) {}

    async getPGN():Promise<string> {
        const responsePGN: Response = await fetch(`/api/chess/PGN`);
        let PGN: string = "";
        if(responsePGN.ok) { PGN = await responsePGN.text()}
        return PGN;
    }

    async getMovePGN(): Promise<string> {
        const resp: Response = await fetch(`api/chess/PGN_THIS`);
        let movePGN: string = "";
        if(resp.ok) { movePGN = await resp.text(); }

        return movePGN;
    }

    render(container: HTMLElement) {
        container.style.display = "grid";
        container.style.gridTemplateRows = `repeat(${this.rows}, 1fr)`;
        container.style.gridTemplateColumns = `repeat(${this.col}, 1fr)`
    }
}