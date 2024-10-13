# Introduction

# Prerequisites to run rust interactively in Jupyter

- Rust installation
- Install EvCxR Jupyter Kernel

## Rust Installation

Installing rust is the first step to run rust interactively in Jupyter.  If you are using linux or mac, please run the following code for installation as stated in Rust official site (https://rust-lang.org)

```
curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh
```

### Windows installation

Please follow this link (https://forge.rust-lang.org/infra/other-installation-methods.html) to download rust installer for windows.

## EvCxR Jupyter Kernel Installation

After rust is ready in desktop, EvCxR Jupyter Kernel is required to be available in Jupyter.  The simple way to install this kernel for Jupyter is using rust package manager cargo as follow:

```
cargo install evcxr_jupyter
evcxr_jupyter --install
```