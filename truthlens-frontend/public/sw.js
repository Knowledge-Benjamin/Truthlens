// Deepfake A.I Detector — Service Worker (PWA offline support)
const CACHE = 'deepfake-ai-v1'
const OFFLINE_URL = '/offline.html'

const PRECACHE = [
  '/',
  '/detect',
  '/learn',
  '/quiz',
  '/spot',
  '/offline.html',
  '/manifest.json',
  '/favicon.svg',
]

self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE).then(cache => cache.addAll(PRECACHE)).then(() => self.skipWaiting())
  )
})

self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(keys.filter(k => k !== CACHE).map(k => caches.delete(k)))
    ).then(() => self.clients.claim())
  )
})

self.addEventListener('fetch', event => {
  const { request } = event
  const url = new URL(request.url)

  // Don't intercept API calls
  if (url.pathname.startsWith('/api/')) return

  event.respondWith(
    caches.match(request).then(cached => {
      if (cached) return cached
      return fetch(request)
        .then(response => {
          if (response.ok && request.method === 'GET') {
            const clone = response.clone()
            caches.open(CACHE).then(c => c.put(request, clone))
          }
          return response
        })
        .catch(() => {
          if (request.mode === 'navigate') {
            return caches.match(OFFLINE_URL)
          }
        })
    })
  )
})
