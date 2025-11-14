// mark JS availability early so CSS fallbacks stay visible without scripts
document.documentElement.classList.add('js');

// fade.js – பக்கம் மெதுவாக தோன்ற
document.addEventListener('DOMContentLoaded', () => {
  document.body.classList.add('is-ready');
  setupPageLoader();

  // navbar.js – ஸ்க்ரோல் ஆனபோது நிழல்
  const navbar = document.querySelector('.navbar-premium');
  const navToggle = document.getElementById('nav-toggle');
  const navMenu = document.getElementById('navMenu');
  const toggleShadow = () => {
    if (!navbar) return;
    navbar.classList.toggle('is-scrolled', window.scrollY > 8);
  };
  toggleShadow();
  window.addEventListener('scroll', toggleShadow);

  // mobile-nav.js – லிங்க் கிளிக்கில் close
  if (navMenu && navToggle) {
    navMenu.querySelectorAll('a').forEach((link) => {
      link.addEventListener('click', () => {
        navToggle.checked = false;
      });
    });
  }

  // reveal.js – எலெமெண்ட்கள் மெதுவாக மேலே வர
  const revealNodes = document.querySelectorAll('[data-reveal]');
  if ('IntersectionObserver' in window) {
    const observer = new IntersectionObserver((entries, io) => {
      entries.forEach((entry) => {
        if (!entry.isIntersecting) return;
        entry.target.classList.add('reveal-active');
        io.unobserve(entry.target);
      });
    }, { threshold: 0.15 });
    revealNodes.forEach((node) => observer.observe(node));
  } else {
    revealNodes.forEach((node) => node.classList.add('reveal-active'));
  }

  // float-label.js – தட்டச்சு செய்ததும் லேபல் மேலே போக
  document.querySelectorAll('[data-float]').forEach((input) => {
    const updateLabelState = () => {
      if (input.value.trim()) {
        input.classList.add('has-value');
      } else {
        input.classList.remove('has-value');
      }
    };
    ['input', 'blur', 'change'].forEach((eventName) => {
      input.addEventListener(eventName, updateLabelState);
    });
    updateLabelState();
  });

  // auth-panel glow – mouse moveல நிழல்
  document.querySelectorAll('.auth-panel--form').forEach((panel) => {
    const updatePanelState = (point) => {
      const rect = panel.getBoundingClientRect();
      const rawX = ((point.clientX - rect.left) / rect.width) * 100;
      const rawY = ((point.clientY - rect.top) / rect.height) * 100;
      const x = Math.min(100, Math.max(0, rawX));
      const y = Math.min(100, Math.max(0, rawY));
      const tiltX = ((x - 50) / 50) * 6;
      const tiltY = ((50 - y) / 50) * 6;
      panel.style.setProperty('--glow-x', `${x}%`);
      panel.style.setProperty('--glow-y', `${y}%`);
      panel.style.setProperty('--tilt-x', `${tiltX}deg`);
      panel.style.setProperty('--tilt-y', `${tiltY}deg`);
      panel.classList.add('is-hovered');
    };

    const resetPanelState = () => {
      panel.style.setProperty('--glow-x', '50%');
      panel.style.setProperty('--glow-y', '50%');
      panel.style.setProperty('--tilt-x', '0deg');
      panel.style.setProperty('--tilt-y', '0deg');
      panel.classList.remove('is-hovered');
    };

    panel.addEventListener('mousemove', (event) => updatePanelState(event));
    panel.addEventListener('mouseleave', resetPanelState);
    panel.addEventListener('touchmove', (event) => {
      const touch = event.touches[0];
      if (!touch) return;
      updatePanelState(touch);
    }, { passive: true });
    panel.addEventListener('touchend', resetPanelState);
  });

  // form-validate.js – காலியான புலங்களுக்கு எச்சரிக்கை
  document.querySelectorAll('form[data-validate]').forEach((form) => {
    form.addEventListener('submit', (event) => {
      const inputs = Array.from(form.querySelectorAll('input[required], textarea[required]'));
      const emptyFields = inputs.filter((input) => !input.value.trim());
      if (emptyFields.length) {
        event.preventDefault();
        emptyFields[0].focus();
        alert('தயவுசெய்து அனைத்து தேவையான புலங்களையும் நிரப்புங்கள்.');
      }
    });
  });
});

function setupPageLoader() {
  if (!document.body) return;

  // avoid duplicating overlay if scripts are imported twice
  if (!document.querySelector('.page-loader')) {
    const loader = document.createElement('div');
    loader.className = 'page-loader';
    loader.setAttribute('aria-hidden', 'true');
    loader.innerHTML = '<div class="page-loader__bar"></div>';
    document.body.appendChild(loader);
  }

  const showLoader = () => {
    requestAnimationFrame(() => document.body.classList.add('is-routing'));
  };
  const hideLoader = () => document.body.classList.remove('is-routing');

  window.addEventListener('beforeunload', () => {
    showLoader();
  }, { capture: true });

  window.addEventListener('pageshow', () => {
    hideLoader();
  });

  // Some flows (e.g., Ajax validation) may cancel submissions; if so, remove the loader.
  document.querySelectorAll('form').forEach((form) => {
    form.addEventListener('submit', (event) => {
      if (form.dataset.skipLoader === 'true') return;
      requestAnimationFrame(() => {
        if (!event.defaultPrevented) {
          showLoader();
        }
      });
    });
  });
}
