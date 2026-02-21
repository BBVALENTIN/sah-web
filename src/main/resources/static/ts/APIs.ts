export async function getAllPGN() {
    const responsePGN = await fetch('/api/chess/PGN_ALL');
    if(responsePGN.ok) {
        const PGN= await responsePGN.text();
        return PGN;
    }
    console.error("Nu s-au citit date din API PGN");
    return undefined;
}

export async function getMovePGN(): Promise<string>{
    let movePGN = "";
    const resp: Response = await fetch(`api/chess/PGN_THIS`);
    if(resp.ok) { movePGN = await resp.text(); }

    return movePGN;
}