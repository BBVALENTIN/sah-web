var r=(t,e,o)=>()=>{if(o)throw o[0];try{return t&&(e=t(t=0)),e}catch(a){throw o=[a],a}};var A=(t,e)=>()=>{try{return e||t((e={exports:{}}).exports,e),e.exports}catch(o){throw e=0,o}};var b=r(()=>{"use strict"});function s(){fetch("/api/lobby/AVAILABLE").then(t=>t.json()).then(t=>{t.forEach(e=>m(e)),y()}).catch(t=>console.error("Loading error:",t))}function u(){let t=new SockJS("/ws");c.stompClient=Stomp.over(t),c.stompClient.connect({},function(){console.log("Connected to the lobby stream"),c.stompClient.subscribe("/topic/global-lobbies",function(e){let o=JSON.parse(e.body);o.lobbyType==="AVAILABLE"?m(o):L(o.lobbyId),y()})},function(e){console.error("STOMP ERROR GLOBAL: ",e)})}function y(){l===0?d.innerText="There are currently no lobbies open":l===1?d.innerText="There is one lobby open":d.innerText=`There are currently ${l} lobbies opened`}function m(t){let e=document.getElementById("lobby-table-body");if(!e){let n=document.getElementById("lobbiesContainer");n&&(n.innerHTML=`
                <table class="lobby-table" style="width: 100%; border-collapse: collapse; margin-top: 20px;">
                    <thead>
                        <tr style="background-color: #080710; text-align: left;">
                            <th style="padding: 10px; border-bottom: 1px solid #080710;">Creator</th>
                            <th style="padding: 10px; border-bottom: 1px solid #080710">Lobby Id</th>
                            <th style="padding: 10px; border-bottom: 1px solid #080710;">Status</th>
                            <th style="padding: 10px; border-bottom: 1px solid #080710;">Action</th>
                        </tr>
                    </thead>
                    <tbody id="lobby-table-body"></tbody>
                </table>
            `,e=document.getElementById("lobby-table-body"))}if(!e){console.error("There's no lobby container.");return}let o=document.getElementById(`lobby-${t.lobbyId}`),a=t.playerWhite?t.playerWhite:t.playerBlack?t.playerBlack:"Unknown";if(o)o.innerHTML=`
           <td data-label="Creator">${a}</td>
            <td data-label="Lobby Id">${t.lobbyId}</td>
            <td data-label="Status">Open</td>
            <td data-label="Action"><button class="join-btn" data-id="${t.lobbyId}">Join</button></td>
        `;else{let n=document.createElement("tr");n.id=`lobby-${t.lobbyId}`,n.innerHTML=`
           <td data-label="Creator">${a}</td>
            <td data-label="Lobby Id">${t.lobbyId}</td>
            <td data-label="Status">Open</td>
            <td data-label="Action"><button class="join-btn" data-id="${t.lobbyId}">Join</button></td>`,l++,e.appendChild(n)}B()}function L(t){let e=document.getElementById(`lobby-${t}`);e&&(l--,e.remove())}function B(){document.querySelectorAll(".join-btn").forEach(e=>{e.removeEventListener("click",i),e.addEventListener("click",i)})}function i(t){let e=t.target.getAttribute("data-id");fetch(`/api/lobby/${e}`,{method:"POST"}).then(o=>o.text()).then(o=>{o==="lobbyFull"?alert("The lobby is already full."):o.startsWith("play=")&&(window.location.href="/"+o)})}function I(){document.getElementById("createLobbyButton")?.addEventListener("click",async()=>{let o=await(await fetch("/api/lobby/create")).text();window.location.href=`/play=${o}`})}var c,l,d,f=r(()=>{"use strict";b();c={stompClient:null,connected:!1},l=0,d=document.getElementById("lobbiesCount")});function N(){let t=document.getElementById("quickPlayNav");t?t.addEventListener("click",async()=>{let o=await(await fetch("/api/lobby/createQuick")).text();window.location.href=`/play=${o}`}):console.log("There is no navbar in your page")}function C(){document.querySelectorAll(".redirectable").forEach(t=>{t.addEventListener("click",()=>{let e=t.dataset.username;window.location.href=`/profile/${e}`})})}function E(){N(),C()}var p=r(()=>{"use strict"});var T=A(()=>{f();p();function h(){s(),u(),I(),E()}document.readyState==="loading"?document.addEventListener("DOMContentLoaded",h):h()});export default T();
