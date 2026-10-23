export type ProductSource = 'CSV' | 'JSON';

export type ValidationError =
    | 'MISSING_SKU'
    | 'MISSING_NAME'
    | 'MISSING_PRICE'
    | 'NEGATIVE_PRICE'
    | 'MISSING_STOCK'
    | 'NEGATIVE_STOCK'
    | 'MISSING_MANUFACTURER';

export type ConflictType =
    | 'DUPLICATE_SKU'
    | 'STOCK_MISMATCH'
    | 'PRICE_MISMATCH'
    | 'NAME_MISMATCH'
    | 'MANUFACTURER_MISMATCH';

export interface ProductRecord {
    sku: string | null;
    name: string | null;
    manufacturer: string | null;
    finalPriceHuf: number | null;
    stock: number | null;
    ean: string | null;
    updatedAt: string | null;
    source: ProductSource;
    valid: boolean;
    errors: ValidationError[];
}

export interface ProductConflict {
    type: ConflictType;
    firstSource: ProductSource;
    firstValue: string | null;
    secondSource: ProductSource;
    secondValue: string | null;
}

export interface Product {
    sku: string | null;
    valid: boolean;
    hasConflicts: boolean;
    records: ProductRecord[];
    conflicts: ProductConflict[];
}

export type ProductSort = 'sku' | 'name' | 'price' | 'stock';