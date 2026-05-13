"use client";
import { useEffect, useState } from "react";
import { ordersApi, type OrderSummary } from "@/features/orders";

export default function CustomerOrderDetailPage({ params }: { params: { id: string } }) {
  const [order, setOrder] = useState<OrderSummary | null>(null);
  useEffect(()=>{ordersApi.getById(params.id).then(setOrder);},[params.id]);
  return <section className="rounded-lg border p-6"><h2 className="text-lg font-medium">Order detail</h2><p>#{params.id} - {order?.status}</p></section>;
}
