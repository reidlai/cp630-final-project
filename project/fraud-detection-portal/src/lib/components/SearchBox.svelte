<script lang="ts">
    import { createEventDispatcher } from 'svelte';
    
    export let initialValue = '';
    let searchInput = initialValue;
    
    const dispatch = createEventDispatcher();

    function handleSubmit(e: Event) {
        e.preventDefault();
        if (searchInput.trim()) {
            dispatch('search', searchInput.trim());
        }
    }
</script>

<form 
    on:submit={handleSubmit}
    class="flex flex-col sm:flex-row gap-4"
>
    <div class="flex-1">
        <input
            type="text"
            bind:value={searchInput}
            placeholder="Enter card number..."
            class="w-full px-4 py-2 text-gray-700 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
        />
    </div>
    <button 
        type="submit"
        class="px-6 py-2 text-white bg-blue-600 rounded-lg hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 transition-colors duration-200"
        disabled={!searchInput.trim()}
    >
        Search
    </button>
</form>
