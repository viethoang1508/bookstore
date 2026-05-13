"use client";

import { useEffect, useState } from "react";

import { adminPromotionsApi, type AdminPromotion } from "@/features/admin-promotions";

export default function AdminPromotionsPage() {
  const [promotions, setPromotions] = useState<AdminPromotion[]>([]);
  const load = async () => setPromotions(await adminPromotionsApi.list());

  useEffect(() => { void load(); }, []);

  return (
    <section className="space-y-4 rounded-lg border p-6">
      <h2 className="text-lg font-medium">Promotions</h2>
      {promotions.map((promotion) => (
        <article key={promotion.id} className="flex items-center justify-between rounded border p-3 text-sm">
          <div>{promotion.code} · {promotion.status}</div>
          <div className="flex gap-3">
            <button type="button" className="underline" onClick={async () => { await adminPromotionsApi.changeStatus(promotion.id, promotion.status === "ACTIVE" ? "INACTIVE" : "ACTIVE"); await load(); }}>Bật/Tắt</button>
            <button type="button" className="text-red-600 underline" onClick={async () => { await adminPromotionsApi.delete(promotion.id); await load(); }}>Xóa</button>
          </div>
        </article>
      ))}
    </section>
  );
}
