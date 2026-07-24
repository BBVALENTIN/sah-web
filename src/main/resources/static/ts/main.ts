import {navbarFuncs} from "./shared/navbar.js";
import {communityFuncs} from "./community/community-funcs.js";
import {profileFuncs} from "./profile/profile-funcs.js";

addEventListener('DOMContentLoaded', async () => {
    navbarFuncs();
    profileFuncs();
    await communityFuncs();
});