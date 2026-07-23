import {navbarFuncs} from "./shared/navbar.js";
import {communityFuncs} from "./community/community-funcs";
import {profileFuncs} from "./profile/profile-funcs";

addEventListener('DOMContentLoaded', async () => {
    navbarFuncs();
    profileFuncs();
    await communityFuncs();
});