<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    export let transactions = [];
    
    const dispatch = createEventDispatcher();

    // Sorting state
    let sortField = 'transactionDatetime';
    let sortDirection = 'desc';
    let filterValues = {
        id: '',
        transactionType: '',
        merchantCity: '',
        fraudDetected: 'all'
    };

    // Format currency
    const formatCurrency = (amount: number) => {
        return new Intl.NumberFormat('en-US', {
            style: 'currency',
            currency: 'USD'
        }).format(amount);
    };

    // Format date
    const formatDate = (dateStr: string) => {
        return new Date(dateStr).toLocaleString();
    };

    // Computed property for filtered and sorted transactions
    $: filteredAndSortedTransactions = [...transactions]
        .filter(t => {
            return (
                (filterValues.id === '' || t.id.toLowerCase().includes(filterValues.id.toLowerCase())) &&
                (filterValues.transactionType === '' || t.transactionType.toLowerCase().includes(filterValues.transactionType.toLowerCase())) &&
                (filterValues.merchantCity === '' || t.merchantCity.toLowerCase().includes(filterValues.merchantCity.toLowerCase())) &&
                (filterValues.fraudDetected === 'all' || 
                 (filterValues.fraudDetected === 'true' && t.fraudDetected) ||
                 (filterValues.fraudDetected === 'false' && !t.fraudDetected))
            );
        })
        .sort((a, b) => {
            const aVal = a[sortField];
            const bVal = b[sortField];
            const modifier = sortDirection === 'asc' ? 1 : -1;
            
            if (typeof aVal === 'boolean') {
                return (aVal === bVal) ? 0 : (aVal ? -1 : 1) * modifier;
            }
            
            return ((aVal > bVal) ? 1 : -1) * modifier;
        });

    function handleSort(field) {
        if (sortField === field) {
            sortDirection = sortDirection === 'asc' ? 'desc' : 'asc';
        } else {
            sortField = field;
            sortDirection = 'asc';
        }
    }

    function getSortIcon(field) {
        if (sortField !== field) return '↕️';
        return sortDirection === 'asc' ? '↑' : '↓';
    }
</script>

<div class="space-y-4">
    <!-- Filters -->
    <div class="grid grid-cols-1 md:grid-cols-4 gap-4 bg-white p-4 rounded-lg shadow">
        <input 
            class="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="Filter ID" 
            bind:value={filterValues.id}
        />
        <input 
            class="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="Filter Type" 
            bind:value={filterValues.transactionType}
        />
        <input 
            class="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            placeholder="Filter City" 
            bind:value={filterValues.merchantCity}
        />
        <select 
            class="px-4 py-2 border rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            bind:value={filterValues.fraudDetected}
        >
            <option value="all">All Transactions</option>
            <option value="true">Fraudulent Only</option>
            <option value="false">Non-Fraudulent Only</option>
        </select>
    </div>

    <!-- Table -->
    <div class="overflow-x-auto bg-white rounded-lg shadow">
        <table class="min-w-full divide-y divide-gray-200">
            <thead class="bg-gray-50">
                <tr>
                    <th class="group px-6 py-3 text-left cursor-pointer" on:click={() => handleSort('id')}>
                        <div class="flex items-center space-x-1">
                            <span class="text-xs font-medium text-gray-500 uppercase tracking-wider">ID</span>
                            <span class="text-gray-400 group-hover:text-gray-600">{getSortIcon('id')}</span>
                        </div>
                    </th>
                    <th class="group px-6 py-3 text-left cursor-pointer" on:click={() => handleSort('transactionDatetime')}>
                        <div class="flex items-center space-x-1">
                            <span class="text-xs font-medium text-gray-500 uppercase tracking-wider">Date</span>
                            <span class="text-gray-400 group-hover:text-gray-600">{getSortIcon('transactionDatetime')}</span>
                        </div>
                    </th>
                    <th class="group px-6 py-3 text-left cursor-pointer" on:click={() => handleSort('transactionAmount')}>
                        <div class="flex items-center space-x-1">
                            <span class="text-xs font-medium text-gray-500 uppercase tracking-wider">Amount</span>
                            <span class="text-gray-400 group-hover:text-gray-600">{getSortIcon('transactionAmount')}</span>
                        </div>
                    </th>
                    <th class="group px-6 py-3 text-left cursor-pointer" on:click={() => handleSort('transactionType')}>
                        <div class="flex items-center space-x-1">
                            <span class="text-xs font-medium text-gray-500 uppercase tracking-wider">Type</span>
                            <span class="text-gray-400 group-hover:text-gray-600">{getSortIcon('transactionType')}</span>
                        </div>
                    </th>
                    <th class="group px-6 py-3 text-left cursor-pointer" on:click={() => handleSort('merchantCity')}>
                        <div class="flex items-center space-x-1">
                            <span class="text-xs font-medium text-gray-500 uppercase tracking-wider">City</span>
                            <span class="text-gray-400 group-hover:text-gray-600">{getSortIcon('merchantCity')}</span>
                        </div>
                    </th>
                    <th class="group px-6 py-3 text-left cursor-pointer" on:click={() => handleSort('fraudDetected')}>
                        <div class="flex items-center space-x-1">
                            <span class="text-xs font-medium text-gray-500 uppercase tracking-wider">Fraud</span>
                            <span class="text-gray-400 group-hover:text-gray-600">{getSortIcon('fraudDetected')}</span>
                        </div>
                    </th>
                </tr>
            </thead>
            <tbody class="bg-white divide-y divide-gray-200">
                {#each filteredAndSortedTransactions as transaction}
                    <tr 
                        class="hover:bg-gray-50 cursor-pointer transition-colors duration-150"
                        on:click={() => dispatch('select', transaction.id)}
                    >
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{transaction.id}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{formatDate(transaction.transactionDatetime)}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{formatCurrency(transaction.transactionAmount)}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{transaction.transactionType}</td>
                        <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{transaction.merchantCity}</td>
                        <td class="px-6 py-4 whitespace-nowrap">
                            {#if transaction.fraudDetected}
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-800">
                                    Yes
                                </span>
                            {:else}
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-green-100 text-green-800">
                                    No
                                </span>
                            {/if}
                        </td>
                    </tr>
                {/each}
            </tbody>
        </table>
    </div>
</div>