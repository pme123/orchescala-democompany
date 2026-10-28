import{r as T,j as h}from"./react-BjFON_JM.js";import{f as c,i as y,j as w,A as O,C as R,b as q,c as K}from"./context-vKzv_xJ_.js";import{p as F}from"./diff-Bw3Md3tL.js";import{aG as u,aW as N,aL as I,aO as L,aX as z,aR as D,aV as W,U as J,bh as M,X as U}from"./index-BIH14M_2.js";import"./msal-DG5e1sgD.js";import"./icons-DU_C22kw.js";import"./feel-D3CuDJS5.js";import"./text-XQH4lAtL.js";const H=`import type { CommentThread } from '../../types';

// The page specification (E15) – declarative, rendered at runtime. A page reads and
// writes a state (state); texts and inputs of the actions are templates with {{pfad}}.
//
// The state additionally contains: query (the parameters of the URL), user (name, email, roles) and
// what the actions store under \`result\`.

/** A condition: \`pfad\`, \`!pfad\`, \`pfad == 'wert'\`, \`pfad != 'wert'\`, \`pfad == true\` – combined with
  * \`&&\` and \`||\`. \`!\` only in front of a path, not in front of a comparison (\`!a == 'x'\` is wrong: \`a != 'x'\`) –
  * the designer and the build report it. Texts in '…' or "…", without escapes (\`"it's"\` instead of \`'it\\'s'\`). */
export type Cond = string;

/** Error texts by HTTP status ("409") and \`default\` – publicly the gateway only returns the status. */
export type Errors = Record<string, string>;

export type Action =
  | { do: 'set'; path: string; value: unknown }
  /** A worker/service: POST /worker/{service} – with \`public\` without login via /public/worker */
  | { do: 'call'; service: string; public?: boolean; input?: unknown; result?: string; errors?: Errors; onError?: Action[] }
  /** Start a process: POST /process/{process}/async */
  | { do: 'start'; process: string; public?: boolean; businessKey?: string; input?: unknown; result?: string; errors?: Errors; onError?: Action[] }
  /** A message: POST /message/{name} – publicly only with businessKey */
  | { do: 'message'; name: string; public?: boolean; businessKey: string; input?: unknown; result?: string; errors?: Errors; onError?: Action[] }
  /** Complete a user task: POST /userTask/{taskKey}/{taskId}/complete */
  | { do: 'completeTask'; taskKey: string; taskId: string; input?: unknown; errors?: Errors; onError?: Action[] };

export type Field = {
  bind: string;
  label: string;
  input?: 'text' | 'email' | 'tel' | 'textarea';
  required?: boolean;
  placeholder?: string;
  visible?: Cond;
};

export type Option = { value: unknown; label: string; hint?: string };

type Base = {
  visible?: Cond;
  /** stable over moving and changing - comments and the change log hang on it; the renderer ignores it */
  id?: string;
};

export type Component = Base &
  (
    | { type: 'heading'; text: string }
    | { type: 'text'; text: string; tone?: 'muted' | 'success' | 'error' | 'info' }
    /** A choice from fixed values – as tiles */
    | { type: 'choice'; bind: string; label?: string; options: Option[]; required?: boolean; onChange?: Action[] }
    /** A choice from a list in the state (e.g. free time slots), grouped */
    | {
        type: 'pick';
        bind: string;
        label?: string;
        items: string;
        groupBy?: { path: string; format?: string };
        itemLabel: string;
        itemHint?: string;
        empty?: string;
        required?: boolean;
      }
    | { type: 'fields'; label?: string; fields: Field[] }
    | { type: 'summary'; label?: string; items: { label: string; value: string; visible?: Cond }[] }
    /** With \`validate\` checks the visible required fields, then runs the actions one after another */
    | { type: 'button'; label: string; actions: Action[]; validate?: boolean; secondary?: boolean }
    | { type: 'section'; label?: string; body: Component[] }
    /** A spinner while the actions run on load */
    | { type: 'loading'; text?: string }
  );

/** \`public\` without login - otherwise with login and one of the roles (without roles: anyone with login). */
export type Access = 'public' | { roles: string[] };

export type Page = {
  path: string;
  title: string;
  access: Access;
  state?: Record<string, unknown>;
  load?: Action[];
  body: Component[];
  /** what the specification knows about the page besides what the app shows - like a process: company
    * and project, epics, comments. The bundle for the app (pages.json) leaves it out. */
  spec?: PageSpec;
};

export type PageSpec = {
  /** Orchescala project, e.g. \`democompany-customer\` */
  project?: string;
  /** IDs of the epics (\`Model.epics\`) this page contributes to */
  epics?: string[];
  comments?: CommentThread[];
};

/** The appearance of the app - e.g. from the bank's website (skill orch-theme-from-site). Everything optional;
  * without it the z9nai style applies. In the banking zone there is no address from outside: the logo is
  * a data: URI, the font a stack of system fonts. */
export type Theme = {
  /** buttons, selected tiles, focus - \`#0b5cab\` */
  primary?: string;
  /** text on \`primary\` - if not given white or black, depending on brightness */
  onPrimary?: string;
  /** background of the page (light) */
  background?: string;
  /** surfaces: cards, sections (light) */
  surface?: string;
  /** text (light) */
  text?: string;
  /** \`sans\`, \`serif\`, \`mono\` or a CSS stack: \`"Frutiger", Arial, sans-serif\` */
  font?: string;
  /** corners: \`none\`, \`sm\`, \`md\`, \`lg\`, \`xl\` */
  radius?: 'none' | 'sm' | 'md' | 'lg' | 'xl';
  /** the logo in the header - a data: URI (up to 200 KB) */
  logo?: string;
  /** light or dark as default - the user can still switch */
  mode?: 'light' | 'dark';
};

export type App = {
  title?: string;
  subtitle?: string;
  theme?: Theme;
  /** The page for / */
  home?: string;
  /** Texts for values – {{wert|label:topic}} */
  labels?: Record<string, Record<string, string>>;
};

export type Pages = { app: App; pages: Page[] };
`,V=(n,r)=>({slug:n,base:r,page:r,openPoints:[]}),j=n=>JSON.stringify(n.base)!==JSON.stringify(n.page),X=n=>{const{spec:r,...t}=n;return t};function S(n){const r=({key:t,block:o,depth:e})=>{const{body:a,...i}=o;return`${"  ".repeat(e)}${t} ${w(i)}`};return U(n.body).map(r).join(`
`)||"(keine Bausteine)"}const v=(n,r,t=!1)=>J(n.page,r.targets,[n.page,...r.others.filter(o=>o.slug!==n.slug).map(o=>o.page)]).filter(o=>t||o.level!=="info"),E=n=>n.length?n.map(r=>`- ${r.level}${r.key!==void 0?` [${r.key}]`:""}: ${r.message}`).join(`
`):"keine",m=["before","after","inside"],d=n=>typeof n=="string"&&/^\d+(\.\d+)*$/.test(n),$={type:"object",description:'A building block as in the catalog (Component) - e.g. {"type":"fields","label":"Kontakt","fields":[…]}. A section with its body.'},G=[{name:"read_page",description:"Reads the draft of the page (without slug) or another page of the app (read only): the JSON and the outline with the keys of the building blocks.",input_schema:{type:"object",properties:{slug:{type:"string",description:"another page - empty for the draft"}}}},{name:"insert_block",description:"Inserts a building block before or after the block `at`, or as the last one inside the section `at`. Without `at` at the end of the page. Answers with the new key, the outline and the findings.",input_schema:{type:"object",properties:{block:$,at:{type:"string",description:'key of a building block, e.g. "2" or "1.0"'},place:{type:"string",enum:m}},required:["block"]}},{name:"update_block",description:"Replaces the building block at `key` by `block` (the whole block, e.g. a fields block with one more field). Answers with the outline and the findings.",input_schema:{type:"object",properties:{key:{type:"string"},block:$},required:["key","block"]}},{name:"move_block",description:"Moves the building block `key` before or after the block `to`, or into the section `to`. Answers with the new key and the outline.",input_schema:{type:"object",properties:{key:{type:"string"},to:{type:"string"},place:{type:"string",enum:m}},required:["key","to","place"]}},{name:"remove_block",description:"Removes the building block `key` (a section with its content).",input_schema:{type:"object",properties:{key:{type:"string"}},required:["key"]}},{name:"update_page",description:'Changes the properties of the page: title, path, access ("public" or {"roles":[…]}), state (the initial state) and load (the actions on load). Only what is given changes; the building blocks stay.',input_schema:{type:"object",properties:{title:{type:"string"},path:{type:"string"},access:{description:'"public" or {"roles": ["…"]}'},state:{type:"object"},load:{type:"array",items:{type:"object"}}}}},{name:"check_page",description:"The findings of the draft as in the designer - errors, warnings and hints. Errors must be gone before the draft goes to the requirements engineer.",input_schema:{type:"object",properties:{}}},{name:"report_open_point",description:"Reports what cannot be done in the spec - above all a service (worker) that is missing in the catalog. Do not invent it; a developer implements it.",input_schema:{type:"object",properties:{text:{type:"string"}},required:["text"]}}];function b(n,r,t,o,e={}){const a=M(r);if(a)return c(n,`Nicht übernommen – so kann der Renderer die Seite nicht zeigen: ${a}`,`abgelehnt: ${a}`);const i={...n,page:r},s=v(i,t);return{draft:i,content:`ok
${w(e)}
Aufbau:
${S(r)}
Befunde:
${E(s)}`,summary:o}}const k=n=>"label"in n&&n.label?`«${n.label}»`:"text"in n&&n.text?`«${n.text}»`:"";function Z(n,r,t,o){const e=y(r)?r:{},a=t.page.body;switch(n){case"read_page":{const i=typeof e.slug=="string"&&e.slug&&e.slug!==t.slug?e.slug:null,s=i?o.others.find(p=>p.slug===i):null;if(i&&!s)return c(t,`Eine Seite «${i}» gibt es nicht. Seiten: ${o.others.map(p=>p.slug).join(", ")}`);const g=(s==null?void 0:s.page)??t.page;return{draft:t,content:`${w(X(g))}
Aufbau:
${S(g)}`,summary:`Seite «${i??t.slug}» gelesen`}}case"insert_block":{if(!y(e.block))return c(t,"«block» fehlt oder ist kein Objekt.");const i=z(e.block),s=m.includes(e.place)?e.place:"after";if(e.at!==void 0&&!(d(e.at)&&u(a,e.at)))return c(t,`Einen Baustein «${String(e.at)}» gibt es nicht.`);const g=e.at===void 0?D(a,i):W(a,i,e.at,s);return b(t,{...t.page,body:g.body},o,`Baustein ${i.type} ${k(i)} eingefügt`.trim(),{key:g.key})}case"update_block":{if(!d(e.key)||!u(a,e.key))return c(t,`Einen Baustein «${String(e.key)}» gibt es nicht.`);if(!y(e.block))return c(t,"«block» fehlt oder ist kein Objekt.");const i=u(a,e.key),s={...e.block,...i.id?{id:i.id}:{}};return b(t,{...t.page,body:L(a,e.key,()=>s)},o,`Baustein ${e.key} ${k(s)} geändert`.replace(/\s+/g," "))}case"move_block":{if(!d(e.key)||!u(a,e.key))return c(t,`Einen Baustein «${String(e.key)}» gibt es nicht.`);if(!d(e.to)||!u(a,e.to))return c(t,`Einen Baustein «${String(e.to)}» gibt es nicht.`);const i=m.includes(e.place)?e.place:"after",s=I(a,e.key,e.to,i);return s.body===a?c(t,"Nicht verschoben – in sich selbst oder an dieselbe Stelle geht nicht."):b(t,{...t.page,body:s.body},o,`Baustein ${e.key} verschoben`,{key:s.key})}case"remove_block":{if(!d(e.key)||!u(a,e.key))return c(t,`Einen Baustein «${String(e.key)}» gibt es nicht.`);const i=u(a,e.key);return b(t,{...t.page,body:N(a,e.key)},o,`Baustein ${i.type} ${k(i)} entfernt`.trim())}case"update_page":{const s=["title","path","access","state","load"].filter(p=>e[p]!==void 0);if(!s.length)return c(t,"Nichts angegeben – title, path, access, state oder load.");const g={...t.page,...Object.fromEntries(s.map(p=>[p,e[p]]))};return b(t,g,o,`Seite geändert: ${s.join(", ")}`)}case"check_page":{const i=v(t,o,!0);return{draft:t,content:E(i),summary:`geprüft: ${i.filter(s=>s.level==="error").length} Fehler, ${i.filter(s=>s.level==="warning").length} Warnungen`}}case"report_open_point":{const i=typeof e.text=="string"?e.text.trim():"";return i?{draft:{...t,openPoints:[...t.openPoints,i]},content:"notiert",summary:`offener Punkt: ${i}`}:c(t,"«text» fehlt.")}default:return c(t,`Ein Werkzeug «${n}» gibt es nicht.`)}}const Q=n=>({tools:G,run:(r,t,o)=>Z(r,t,o,n),remainingErrors:r=>Y(r,n)});function Y(n,r){if(!j(n))return[];const t=e=>v({...n,page:e},r).filter(a=>a.level==="error").map(a=>a.message),o=new Set(t(n.base));return t(n.page).filter(e=>!o.has(e))}const A=n=>({...n,spec:n.spec?{...n.spec,comments:void 0}:void 0});function le({isDark:n,apiKey:r,slug:t,page:o,pages:e,app:a,targets:i,draft:s,onDraft:g,onAccept:p,onSelect:_,initialPrompt:C,onInitialPrompt:B}){const P=T.useMemo(()=>s?F(s.base,s.page):[],[s]),x=[...e.filter(l=>l.slug!==t),{slug:t,page:o}];return h.jsx(O,{isDark:n,apiKey:r,draft:s,onDraft:g,initialPrompt:C,onInitialPrompt:B,system:()=>K(H,i,x,a),kit:Q({targets:i,others:x}),start:()=>V(t,o),changed:j,openPoints:l=>l.openPoints,firstPrompt:l=>q(t,l,!0),onAccept:(l,f)=>{if(JSON.stringify(A(o))!==JSON.stringify(A(l.base)))return{refused:"Die Seite hat sich seit dem Entwurf geändert (neu geladen) – Entwurf verwerfen und Claude nochmals fragen."};p(l.page,f)},acceptedText:"Übernommen – rückgängig mit ⌘Z.",placeholder:"Was soll sich ändern?",hint:h.jsxs(h.Fragment,{children:[h.jsx("p",{children:"Beschreibe, was sich an der Seite ändern soll – z.B. «Nimm im Formular ein Feld für die bevorzugte Filiale auf»."}),h.jsx("p",{children:"Claude ändert einen Entwurf und prüft ihn mit den Befunden des Designers. Du siehst ihn in der Vorschau und übernimmst oder verwirfst ihn."})]}),changes:P.map((l,f)=>h.jsx(R,{isDark:n,kind:l.kind,label:l.label,detail:l.detail,onClick:l.key!==void 0&&l.kind!=="removed"?()=>_(l.key):void 0},f))})}export{le as AiPanel};
