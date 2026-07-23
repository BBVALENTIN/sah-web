import {initEngineApp} from "./enginep-funcs.js";
import {navbarFuncs} from "../shared/navbar.js";

async function start() {
    await initEngineApp();
    navbarFuncs();
}
if(document.readyState === 'loading')
    document.addEventListener('DOMContentLoaded', start)
else
    await start();