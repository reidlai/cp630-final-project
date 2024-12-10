import { sveltekit } from '@sveltejs/kit/vite';
import { defineConfig } from 'vite';

export default defineConfig({
    plugins: [sveltekit()],
    server: {
        fs: {
            allow: ['..']
        }
    },
    define: {
        'process.env.NODE_ENV': '"development"'
    }
});