"use client";
import { useState } from "react";
import { checkoutApi } from "@/features/checkout";

export default function CustomerCheckoutPage() {
  const [receiverName,setReceiverName]=useState("");const [receiverPhone,setReceiverPhone]=useState("");const [shippingAddress,setShippingAddress]=useState("");const [bookId,setBookId]=useState("");const [quantity,setQuantity]=useState(1);const [msg,setMsg]=useState("");
  const body={receiverName,receiverPhone,shippingAddress,items:[{bookId,quantity}]};
  return <section className="rounded-lg border p-6 space-y-2"><h2 className="text-lg font-medium">Checkout</h2><input placeholder="Book ID" value={bookId} onChange={(e)=>setBookId(e.target.value)} className="rounded border px-2 py-1"/><input placeholder="Receiver name" value={receiverName} onChange={(e)=>setReceiverName(e.target.value)} className="rounded border px-2 py-1"/><input placeholder="Phone" value={receiverPhone} onChange={(e)=>setReceiverPhone(e.target.value)} className="rounded border px-2 py-1"/><input placeholder="Address" value={shippingAddress} onChange={(e)=>setShippingAddress(e.target.value)} className="rounded border px-2 py-1"/><button className="rounded bg-black text-white px-3 py-1" onClick={async()=>{await checkoutApi.preview(body);setMsg('Preview success')}}>Preview</button><button className="rounded border px-3 py-1" onClick={async()=>{await checkoutApi.place(body);setMsg('Order placed')}}>Place order</button><p>{msg}</p></section>;
}
