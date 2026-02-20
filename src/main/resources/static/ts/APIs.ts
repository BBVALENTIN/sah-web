export async function getAllPGN() {
    const responsePGN = await fetch('/api/chess/PGN_ALL');
    if(responsePGN.ok) {
        const PGN= await responsePGN.text();
        return PGN;
    }
    console.error("Nu s-au citit date din API PGN");
    return undefined;
}