
export function useTableScroll(offset = 120) {
  const tableWrapRef = useRef<HTMLDivElement>(null)
  const [scrollY, setScrollY] = useState(300)

  useLayoutEffect(() => {
    const el = tableWrapRef.current
    if (!el) return
    const ro = new ResizeObserver(([entry]) => {
      setScrollY(Math.max(100, entry.contentRect.height - offset))
    })
    ro.observe(el)
    return () => ro.disconnect()
  }, [offset])

  return { tableWrapRef, scrollY }
}
