import {initPractice} from "./practiceFuncs.js";
import {navbarFuncs} from "../shared/navbar.js";

addEventListener('DOMContentLoaded', async () => {
    navbarFuncs();
    await initPractice();
});