import type { Product, ProductSort } from '../types/product';

export interface ProductQuery {
    filter: string;
    sort: ProductSort;
    onlyValid: boolean;
}

export async function fetchProducts(
    query: ProductQuery,
    signal?: AbortSignal
): Promise<Product[]> {
    const params = new URLSearchParams({
        filter: query.filter,
        sort: query.sort,
        onlyValid: String(query.onlyValid),
    });

    const response = await fetch(
        `/products?${params.toString()}`,
        { signal }
    );

    if (!response.ok) {
        throw new Error(
            `Failed to load products (HTTP ${response.status})`
        );
    }

    return response.json() as Promise<Product[]>;
}