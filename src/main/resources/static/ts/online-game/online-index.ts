import {initializeApp} from "./onlineg-funcs.js";
import {navbarFuncs} from "../shared/navbar.js";

async function start() {
    await initializeApp();
    navbarFuncs();
}

if(document.readyState === 'loading')
    document.addEventListener('DOMContentLoaded', start);
else
    await start();