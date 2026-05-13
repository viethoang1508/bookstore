"use client";
import { useEffect, useState } from "react";
import { profileApi, type UserResponse } from "@/features/profile";

export default function CustomerProfilePage() {
  const [profile,setProfile]=useState<UserResponse | null>(null);
  const [fullName,setFullName]=useState("");
  const [username,setUsername]=useState("");
  const [phone,setPhone]=useState("");
  useEffect(()=>{profileApi.me().then((d)=>{setProfile(d);setFullName(d.fullName??"");setUsername(d.username??"");setPhone(d.phone??"");});},[]);
  return <section className="rounded-lg border p-6 space-y-2"><h2 className="text-lg font-medium">Profile</h2><input value={fullName} onChange={(e)=>setFullName(e.target.value)} className="rounded border px-2 py-1"/><input value={username} onChange={(e)=>setUsername(e.target.value)} className="rounded border px-2 py-1"/><input value={phone} onChange={(e)=>setPhone(e.target.value)} className="rounded border px-2 py-1"/><button className="rounded bg-black text-white px-3 py-1" onClick={async()=>setProfile(await profileApi.update({fullName,username,phone}))}>Save</button><p>{profile?.email}</p></section>;
}
