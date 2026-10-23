import {Fragment, useState} from 'react';
import type {Product} from '../types/product';

interface Props {
    products: Product[];
}

const formatPrice = (price: number | null) =>
    price == null
        ? '—'
        : `${price.toLocaleString('hu-HU')} Ft`;

const formatDate = (date: string | null) =>
    date ? new Date(date).toLocaleString('hu-HU') : '—';

const formatValue = (value: string | number | null) =>
    value == null ? '—' : String(value);

export default function ProductTable({products}: Props) {
    const [expandedRows, setExpandedRows] = useState<Set<number>>(
        new Set()
    );

    function toggleRow(index: number) {
        setExpandedRows(previous => {
            const next = new Set(previous);

            if (next.has(index)) {
                next.delete(index);
            } else {
                next.add(index);
            }

            return next;
        });
    }

    if (products.length === 0) {
        return (
            <div className="alert alert-info">
                No matching products found.
            </div>
        );
    }

    return (
        <div className="card border-0 shadow-sm">
            <div className="table-responsive">
                <table className="table table-hover align-middle mb-0">
                    <thead className="table-light">
                    <tr>
                        <th>SKU</th>
                        <th>Name</th>
                        <th>Manufacturer</th>
                        <th className="text-end">Price</th>
                        <th className="text-end">Stock</th>
                        <th>Status</th>
                    </tr>
                    </thead>

                    <tbody>
                    {products.map((product, index) => {
                        const primary = product.records[0];
                        const expanded = expandedRows.has(index);

                        return (
                            <Fragment key={`${product.sku ?? 'missing'}-${index}`}>
                                <tr>
                                    <td className="fw-semibold">
                                        {product.sku ?? '—'}
                                    </td>

                                    <td>{primary.name ?? '—'}</td>

                                    <td>{primary.manufacturer ?? '—'}</td>

                                    <td className="text-end">
                                        {formatPrice(primary.finalPriceHuf)}
                                    </td>

                                    <td className="text-end">
                                        {formatValue(primary.stock)}
                                    </td>

                                    <td>
                                        <div className="d-flex flex-wrap align-items-center gap-2">
                        <span
                            className={`badge ${
                                product.valid
                                    ? 'text-bg-success'
                                    : 'text-bg-danger'
                            }`}
                        >
                          {product.valid ? 'Valid' : 'Invalid'}
                        </span>

                                            {product.hasConflicts && (
                                                <span className="badge text-bg-warning">
                            Conflict
                          </span>
                                            )}

                                            <button
                                                type="button"
                                                className="btn btn-sm btn-outline-primary"
                                                onClick={() => toggleRow(index)}
                                                aria-expanded={expanded}
                                            >
                                                {expanded ? 'Hide' : 'Details'}
                                            </button>
                                        </div>
                                    </td>
                                </tr>

                                {expanded && (
                                    <tr className="details-row">
                                        <td colSpan={6} className="p-4">
                                            <div className="row g-3">
                                                {product.records.map((record, recordIndex) => (
                                                    <div
                                                        key={recordIndex}
                                                        className="col-12 col-lg-6"
                                                    >
                                                        <div className="card h-100">
                                                            <div className="card-header d-flex justify-content-between">
                                                                <strong>{record.source} source</strong>

                                                                <span
                                                                    className={`badge ${
                                                                        record.valid
                                                                            ? 'text-bg-success'
                                                                            : 'text-bg-danger'
                                                                    }`}
                                                                >
                                    {record.valid ? 'Valid' : 'Invalid'}
                                  </span>
                                                            </div>

                                                            <div className="card-body">
                                                                <dl className="row mb-0">
                                                                    <dt className="col-sm-5">Name</dt>
                                                                    <dd className="col-sm-7">
                                                                        {record.name ?? '—'}
                                                                    </dd>

                                                                    <dt className="col-sm-5">Manufacturer</dt>
                                                                    <dd className="col-sm-7">
                                                                        {record.manufacturer ?? '—'}
                                                                    </dd>

                                                                    <dt className="col-sm-5">Price</dt>
                                                                    <dd className="col-sm-7">
                                                                        {formatPrice(record.finalPriceHuf)}
                                                                    </dd>

                                                                    <dt className="col-sm-5">Stock</dt>
                                                                    <dd className="col-sm-7">
                                                                        {formatValue(record.stock)}
                                                                    </dd>

                                                                    <dt className="col-sm-5">EAN</dt>
                                                                    <dd className="col-sm-7">
                                                                        {record.ean ?? '—'}
                                                                    </dd>

                                                                    <dt className="col-sm-5">Updated</dt>
                                                                    <dd className="col-sm-7">
                                                                        {formatDate(record.updatedAt)}
                                                                    </dd>
                                                                </dl>

                                                                {record.errors.length > 0 && (
                                                                    <div className="alert alert-danger mt-3 mb-0">
                                                                        <strong>Validation errors</strong>
                                                                        <ul className="mb-0 mt-2">
                                                                            {record.errors.map(error => (
                                                                                <li key={error}>{error}</li>
                                                                            ))}
                                                                        </ul>
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>

                                            {product.conflicts.length > 0 && (
                                                <div className="alert alert-warning mt-3 mb-0">
                                                    <h6 className="alert-heading">
                                                        Data conflicts
                                                    </h6>

                                                    <div className="table-responsive">
                                                        <table className="table table-sm table-bordered mb-0">
                                                            <thead>
                                                            <tr>
                                                                <th>Conflict</th>
                                                                <th>First source</th>
                                                                <th>First value</th>
                                                                <th>Second source</th>
                                                                <th>Second value</th>
                                                            </tr>
                                                            </thead>

                                                            <tbody>
                                                            {product.conflicts.map((conflict, i) => (
                                                                <tr key={i}>
                                                                    <td>{conflict.type}</td>
                                                                    <td>{conflict.firstSource}</td>
                                                                    <td>
                                                                        {formatValue(conflict.firstValue)}
                                                                    </td>
                                                                    <td>{conflict.secondSource}</td>
                                                                    <td>
                                                                        {formatValue(conflict.secondValue)}
                                                                    </td>
                                                                </tr>
                                                            ))}
                                                            </tbody>
                                                        </table>
                                                    </div>
                                                </div>
                                            )}
                                        </td>
                                    </tr>
                                )}
                            </Fragment>
                        );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}