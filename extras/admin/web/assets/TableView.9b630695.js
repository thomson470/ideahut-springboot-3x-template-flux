import{Q as y,b as d,a as w}from"./format.f63947d2.js";import{Q}from"./QTooltip.8dadf5a5.js";import{_ as q,Z as a,r as m,a5 as i,a2 as u,a3 as s,f as t,aK as f,a7 as x,a8 as _,C as g,a6 as v,ab as V,F as C,ac as O,aL as S,aM as $,aJ as j}from"./index.163e81c6.js";import{C as k}from"./ClosePopup.5dc9f167.js";const B={props:["parameters"],setup(){return{util:a,scope:m(null),fields:m([])}},created(){let e=this,o=a.isObject(e.parameters)?e.parameters:{};e.scope=a.isObject(o.scope)?o.scope:{};let p=a.isArray(o.columns)?o.columns:e.scope.cols,c=a.isObject(e.scope.row)?e.scope.row:{};e.fields=[];for(const r of p){let l=a.getFieldValue(r.field,c);a.isDefined(l)&&a.isFunction(r.format)&&(l=r.format(l,c)),a.isObject(l)&&(l=JSON.stringify(l)),e.fields.push({label:r.label,value:l})}}};function F(e,o,p,c,r,l){return i(),u(j,{style:$("width: "+(e.$q.screen.lt.md?"100%;":"50%;"))},{default:s(()=>[t(f,{class:"q-pa-none header-main"},{default:s(()=>[t(y,{class:"q-pr-none"},{default:s(()=>[t(d,null,{default:s(()=>[t(w,{class:"text-h6 text-white"},{default:s(()=>[x(_(),1)]),_:1})]),_:1}),t(d,{side:""},{default:s(()=>[g((i(),u(v,{class:"text-caption text-white q-pl-xs q-pr-xs q-mr-xs",flat:"",round:"",glossy:"",icon:"close"},{default:s(()=>[t(Q,null,{default:s(()=>[x(_(e.$t("label.close")),1)]),_:1})]),_:1})),[[k]])]),_:1})]),_:1})]),_:1}),t(f,{style:{"max-height":"80vh"},class:"q-pa-xs q-mt-xs scroll"},{default:s(()=>[(i(!0),V(C,null,O(c.fields,(n,b)=>(i(),u(S,{type:"text",key:b,label:n.label,modelValue:n.value,"onUpdate:modelValue":h=>n.value=h,readonly:"",filled:"",autogrow:"",class:"q-mb-xs",style:{"max-height":"200px",overflow:"scroll"}},null,8,["label","modelValue","onUpdate:modelValue"]))),128))]),_:1})]),_:1},8,["style"])}var T=q(B,[["render",F]]);export{T as default};