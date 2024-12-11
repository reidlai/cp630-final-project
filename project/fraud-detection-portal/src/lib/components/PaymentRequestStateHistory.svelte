<script lang="ts">
    import { onMount } from 'svelte';
    import type { PaymentRequestStatus } from '$lib/types';
    import { getTransactionStateHistory, updateTransactionState } from '$lib/api';
    
    export let transactionId: string;

    let statuses: PaymentRequestStatus[] = [];
    let error: string | null = null;
    let loading = true;
    let isUpdating = false;

    onMount(async () => {
        try {

            statuses = await getTransactionStateHistory(transactionId);
            console.debug("statuses.length", statuses.length);
            // Sort statusData: entries with deletedAt null first, then by deletedAt descending

            loading = false;
        } catch (e) {
            error = e instanceof Error ? e.message : 'An error occurred';
            loading = false;
        }
    });

    async function handleStateChange(newState: string) {
        try {
            isUpdating = true;
            const response = await updateTransactionState({
                id: transactionId,
                state: newState
            });
            isUpdating = false;
            loading = true;
            // Refresh status list after update
            statuses = await getTransactionStateHistory(transactionId);
            loading = false;
       
            
        } catch (e) {
            error = e instanceof Error ? e.message : 'Failed to update status';
            loading = false;
        } finally {
            isUpdating = false;
            loading = false;
        }
    }
</script>

<div class="max-w-4xl mx-auto p-4 space-y-6">
    <!-- Loading State -->
    {#if loading}
        <div class="flex justify-center items-center h-32">
            <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
        </div>
    <!-- Error State -->
    {:else if error}
        <div class="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">
            <p>{error}</p>
            <button 
                class="mt-2 text-sm text-red-600 hover:text-red-800 underline"
                on:click={() => window.location.reload()}
            >
                Retry
            </button>
        </div>
    <!-- Content -->
    {:else}
        <!-- Payment Request Status History Table -->
        <div class="bg-white rounded-lg shadow overflow-hidden">
            <div class="px-6 py-4 border-b border-gray-200">
                <h3 class="text-lg font-medium text-gray-900">Status History</h3>
            </div>
            <div class="overflow-x-auto">
                <table class="min-w-full divide-y divide-gray-200">
                    <thead class="bg-gray-50">
                        <tr>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">State</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Created</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Updated</th>
                            <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Deleted</th>
                        </tr>
                    </thead>
                    <tbody class="bg-white divide-y divide-gray-200">
                        {#each statuses as status}
                            <tr>
                                <td class="px-6 py-4 whitespace-nowrap">
                                    <span class="px-2 py-1 text-sm rounded-full
                                        {status.state === 'ACCEPTED' ? 'bg-green-100 text-green-800' : 
                                         status.state === 'ONHOLD' ? 'bg-yellow-100 text-yellow-800' :
                                         'bg-gray-100 text-gray-800'}">
                                        {status.state}
                                    </span>
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {new Date(status.createdAt).toLocaleString()}
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {new Date(status.updatedAt).toLocaleString()}
                                </td>
                                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {status.deletedAt ? new Date(status.deletedAt).toLocaleString() : '-'}
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>
        </div>

        <!-- Action Buttons -->
        <div class="flex justify-end space-x-4 mt-6">
            {#if isUpdating}
                <div class="flex items-center space-x-2">
                    <div class="animate-spin h-5 w-5 border-2 border-blue-500 rounded-full border-t-transparent"></div>
                    <span class="text-gray-600">Updating status...</span>
                </div>
            {:else}
                <button
                    class="px-4 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 focus:outline-none focus:ring-2 focus:ring-yellow-500 focus:ring-offset-2 disabled:opacity-50"
                    on:click={() => handleStateChange('ONHOLD')}
                    disabled={isUpdating}
                >
                    Put On Hold
                </button>
                <button
                    class="px-4 py-2 bg-green-500 text-white rounded-lg hover:bg-green-600 focus:outline-none focus:ring-2 focus:ring-green-500 focus:ring-offset-2 disabled:opacity-50"
                    on:click={() => handleStateChange('ACCEPTED')}
                    disabled={isUpdating}
                >
                    Accept
                </button>
            {/if}
        </div>
    {/if}
</div>