/* ===== Index interactions: parallax, fade, ripple ===== */

/* Parallax tilt on hero card */
(() => {
  const card = document.querySelector('.quiz-card');
  if (!card) return;
  const strength = 10;
  card.addEventListener('mousemove', (e) => {
    const r = card.getBoundingClientRect();
    const cx = e.clientX - r.left, cy = e.clientY - r.top;
    const rx = ((cy / r.height) - 0.5) * -strength;
    const ry = ((cx / r.width)  - 0.5) *  strength;
    card.style.transform = `perspective(900px) rotateX(${rx}deg) rotateY(${ry}deg) translateY(-2px)`;
  });
  card.addEventListener('mouseleave', () => card.style.transform = '');
})();

/* Fade-in on load */
(() => {
  const root = document.querySelector('.quiz-card');
  if (!root) return;
  root.style.opacity = '0'; root.style.transform = 'translateY(10px)';
  requestAnimationFrame(() => {
    root.style.transition = 'opacity .5s ease, transform .5s ease';
    root.style.opacity = '1'; root.style.transform = 'translateY(0)';
  });
})();

/* Ripple on all primary buttons (start, login, register) */
(() => {
  const targets = document.querySelectorAll('.quiz-start-btn, .btn-register-solid, .btn-login-outline');
  if (!targets.length) return;

  targets.forEach(btn => {
    btn.style.position = 'relative';
    btn.style.overflow = 'hidden';
    btn.addEventListener('click', function (e) {
      const r = document.createElement('span');
      const size = Math.max(this.offsetWidth, this.offsetHeight);
      r.style.width = r.style.height = size + 'px';
      r.style.position = 'absolute';
      r.style.left = e.offsetX - size / 2 + 'px';
      r.style.top  = e.offsetY - size / 2 + 'px';
      r.style.borderRadius = '50%';
      r.style.background = 'rgba(255,255,255,.25)';
      r.style.transform = 'scale(0)';
      r.style.transition = 'transform .4s ease, opacity .6s ease';
      r.style.pointerEvents = 'none';
      this.appendChild(r);
      requestAnimationFrame(() => r.style.transform = 'scale(1.6)');
      setTimeout(() => r.style.opacity = '0', 200);
      setTimeout(() => r.remove(), 650);
    });
  });
})();

/* Reveal subtitle + hint on view */
(() => {
  const els = document.querySelectorAll('.quiz-subtitle, .quiz-hint');
  if (!('IntersectionObserver' in window) || !els.length) return;
  els.forEach(el => { el.style.opacity = '0'; el.style.transform = 'translateY(6px)'; });
  const io = new IntersectionObserver((entries) => {
    entries.forEach(e => {
      if (e.isIntersecting) {
        e.target.style.transition = 'opacity .45s ease, transform .45s ease';
        e.target.style.opacity = '1';
        e.target.style.transform = 'translateY(0)';
        io.unobserve(e.target);
      }
    });
  }, { threshold: .1 });
  els.forEach(el => io.observe(el));
})();
