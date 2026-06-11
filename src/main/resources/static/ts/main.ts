import {navbarFuncs} from "./shared/navbar.js";
import {communityFuncs} from "./community/community.js";
import {profileFuncs} from "./profile/profile.js";

addEventListener('DOMContentLoaded', async () => {
    navbarFuncs();
    profileFuncs();
    await communityFuncs();
});