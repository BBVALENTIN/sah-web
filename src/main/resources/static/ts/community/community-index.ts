import {communityFuncs} from "./community-funcs.js";
import {navbarFuncs} from "../shared/navbar";

export async function start() {
    await communityFuncs(); // should maybe separate community from post page for better understanding
    navbarFuncs();
}

if(document.readyState === "loading")
    document.addEventListener('DOMContentLoaded', start);
else
    await start();