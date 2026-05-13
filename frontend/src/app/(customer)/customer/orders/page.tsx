"use client";
import Link from "next/link";
import { useEffect, useState } from "react";
import { ordersApi, type OrderSummary } from "@/features/orders";

export default function CustomerOrdersPage() {
  const [orders,setOrders]=useState<OrderSummary[]>([]);
  useEffect(()=>{ordersApi.list().then(setOrders);},[]);
  return <section className="rounded-lg border p-6"><h2 className="text-lg font-medium">Order history</h2><div className="mt-3 space-y-2">{orders.map(o=><Link key={o.orderId} href={`/customer/orders/${o.orderId}`} className="block rounded border p-2">#{o.orderId} - {o.status}</Link>)}</div></section>
}
