"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
import { booksApi } from "@/features/books";
import { cartApi } from "@/features/cart";
import type { BookSummary } from "@/types";

export default function BookDetailPage({ params }: { params: { id: string } }) {
  const [book, setBook] = useState<BookSummary | null>(null);
  const [qty, setQty] = useState(1);
  const [msg, setMsg] = useState("");
  useEffect(()=>{booksApi.list().then((list)=>setBook(list.find((x)=>x.id===params.id) ?? null));},[params.id]);
  const add = async()=>{ await cartApi.addItem(params.id, qty); setMsg("Added to cart"); };
  return <div className="space-y-3"><h1 className="text-2xl font-semibold">Book detail</h1>{book?<div className="rounded border p-3"><div>{book.title}</div><div>{String(book.price ?? 0)}</div><input type="number" min={1} value={qty} onChange={(e)=>setQty(Number(e.target.value)||1)} className="mt-2 rounded border px-2 py-1" /><button onClick={()=>void add()} className="ml-2 rounded bg-black px-3 py-1 text-white">Add to cart</button></div>:<p>Book not found.</p>}<p>{msg}</p><Link href="/books" className="underline">Back</Link></div>;
}
