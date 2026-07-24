import { initPractice } from "./practice-funcs.js";
import { navbarFuncs } from "../shared/navbar.js";

async function start() {
    navbarFuncs();
    await initPractice();
}

if(document.readyState === 'loading')
    document.addEventListener('DOMContentLoaded', start);
else
    await start();