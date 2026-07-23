import {connectWS, handleCreate, loadLobbies} from "./lobby-funcs.js";
import {navbarFuncs} from "../shared/navbar.js";

function start() {
    loadLobbies();
    connectWS();
    handleCreate();
    navbarFuncs();
}

if(document.readyState === "loading")
    document.addEventListener('DOMContentLoaded', start);
else
    start();