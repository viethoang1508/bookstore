"use client";
import { useEffect, useState } from "react";
import { cartApi, type CartResponse } from "@/features/cart";

export default function CustomerCartPage() {
  const [cart, setCart] = useState<CartResponse | null>(null);
  const load = async()=> setCart(await cartApi.get());
  useEffect(()=>{void load();},[]);
  return <section className="space-y-3 rounded-lg border p-6"><h2 className="text-lg font-medium">Cart</h2>{cart?.items?.map((i)=><div key={i.bookId} className="flex gap-2"><span>{i.bookName ?? i.bookId}</span><input type="number" min={1} value={i.quantity} onChange={async(e)=>{await cartApi.updateItem(i.bookId, Number(e.target.value)||1); await load();}} className="w-16 rounded border"/><button className="text-red-600" onClick={async()=>{await cartApi.deleteItem(i.bookId); await load();}}>Delete</button></div>)}</section>;
}
