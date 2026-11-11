# Eigenfaces — Image Reconstruction & Recognition System

A Java-based image processing application developed as a **1st-semester academic project** at **ISEP** for **LAPR1 (Laboratório/Projeto I)**. The project applies **linear algebra, eigendecomposition, and the eigenfaces technique** to reconstruct grayscale images, compare facial images, and generate new images from a dataset.

## Overview

This application explores how images can be represented in a lower-dimensional space using eigenvectors and eigenvalues. By treating grayscale images as numerical matrices, it implements mathematical techniques for image reconstruction and similarity-based identification.

The project combines four functionalities:

- **Matrix eigendecomposition** — Approximate symmetric matrices using a selected number of eigenvalues and eigenvectors.
- **Image reconstruction** — Build eigenfaces from a dataset and reconstruct the original images using a reduced set of components.
- **Image identification** — Find the closest matching image in a dataset by comparing eigenface weight vectors.
- **Image generation** — Generate a new image using a mean image and eigenface-derived components.

The application provides a **Portuguese-language command-line interface** and supports both interactive and command-line execution modes.

## Features

### Matrix Eigendecomposition

- Load symmetric matrices from CSV files.
- Compute eigenvalues and eigenvectors using **Apache Commons Math**.
- Select the number of components used to approximate the original matrix.
- Reconstruct matrices using the eigendecomposition formula `A ≈ PₖDₖPₖᵀ`.
- Calculate **Mean Absolute Error (MAE)** to evaluate reconstruction quality.
- Export the reconstructed matrix to CSV.

### Eigenfaces & Image Reconstruction

- Load a dataset of grayscale images represented as CSV matrices.
- Convert images into vectors and calculate the mean image.
- Center the image data and construct a reduced covariance-related matrix using `AᵀA`.
- Compute and normalize eigenface vectors.
- Reconstruct images from a configurable number of eigenfaces and their weights.
- Calculate MAE for each reconstructed image.
- Export eigenfaces and reconstructed images in **CSV and JPG** formats.

### Image Identification

- Project a query image and dataset images into an eigenface-based representation.
- Calculate the corresponding weight vectors (omega vectors).
- Compare images using **Euclidean distance** between their weight vectors.
- Identify the dataset image with the smallest distance to the query.
- Export the closest match as a JPG image.

### Image Generation

- Combine a dataset's mean image with eigenface-derived components.
- Allow the user to select the number of components used.
- Generate and export a new grayscale image as JPG.

## Tech Stack

| **Category** | **Technology** |
| --- | --- |
| **Language** | Java |
| **Mathematics** | Linear algebra, eigendecomposition, eigenfaces |
| **Library** | Apache Commons Math 3.6.1 |
| **Image Processing** | Java `BufferedImage`, `ImageIO` |
| **Data Formats** | CSV (input/output), JPG (output) |
| **Interface** | Command-line application (interactive menus and arguments) |
| **Development Environment** | IntelliJ IDEA project configuration |
| **Version Control** | Git / GitHub (academic workflow) |

## Technical Approach

### 1. Matrix Approximation

For a symmetric matrix, the application approximates the original data using a selected subset of eigenvectors and eigenvalues:

```text
A ≈ Pₖ × Dₖ × Pₖᵀ
```

The approximation is evaluated using **Mean Absolute Error**, enabling comparisons between reconstructions with different numbers of components.

### 2. Eigenfaces Pipeline

```text
CSV Image Dataset
       |
       v
Convert Images to Column Vectors
       |
       v
Calculate Mean Image and Center the Dataset
       |
       v
Construct AᵀA and Compute Eigenvectors
       |
       v
Derive and Normalize Eigenfaces
       |
       +------------------------+
       |                        |
       v                        v
Project & Reconstruct      Project Query Image
Dataset Images             and Dataset Images
       |                        |
       v                        v
CSV / JPG + MAE            Euclidean Distance
                                |
                                v
                           Closest Match (JPG)
```

The implementation uses the smaller `AᵀA` matrix when calculating eigenvectors, avoiding direct eigendecomposition of a potentially much larger pixel-space matrix.

### 3. Similarity-Based Identification

Each image is represented by weights describing its projection onto the selected eigenfaces. For a query image, the application calculates the **Euclidean distance** between its weight vector and the corresponding vectors of the dataset images, selecting the nearest match.

This is **nearest-neighbor image matching**, not a trained modern deep-learning face detector or a verified identity-authentication system.

## Project Structure

```text
├── src/
│   ├── Main.java                      # Interactive menu and CLI dispatch
│   ├── Decomposicao.java              # Eigendecomposition and matrix utilities
│   ├── Covariancias.java              # Eigenface computation and reconstruction
│   ├── IdentificacaoEigenfaces.java   # Image identification by vector distance
│   ├── GeradorDeImagem.java           # Image generation from eigenface components
│   ├── CSVFile.java                   # CSV reading/writing and directory creation
│   ├── ImageWriter.java               # JPG image output
│   └── META-INF/MANIFEST.MF           # Main-Class: Main
├── ImageBase/                         # 39 sample grayscale image CSV files
├── lapr1-DIJ-06/                      # Generated/output files from the project
├── imagemDigital.java                 # Additional image-related source file
├── Projeto-EigenFaces.iml             # IntelliJ IDEA module configuration
└── .gitignore
```

## How to Run

### Prerequisites

- A **Java Development Kit (JDK)** with `java` and `javac` available.
- **Apache Commons Math 3.6.1** (`commons-math3-3.6.1.jar`).
- A terminal or IntelliJ IDEA.

> **Dependency note:** This repository does not include a Maven/Gradle build file or the Apache Commons Math JAR. The original IntelliJ module refers to a JAR in the original developer's local `Downloads` directory. Configure the library locally before building. The Java version used by the original developers is not established by the repository.

### Build from the Command Line

From the repository root, create a `lib/` directory and place `commons-math3-3.6.1.jar` inside it, then run:

**Linux / macOS**

```bash
mkdir -p build
javac -cp "lib/commons-math3-3.6.1.jar" -d build src/*.java
java -cp "build:lib/commons-math3-3.6.1.jar" Main
```

**Windows PowerShell**

```powershell
New-Item -ItemType Directory -Force build | Out-Null
javac -cp "lib/commons-math3-3.6.1.jar" -d build src/*.java
java -cp "build;lib/commons-math3-3.6.1.jar" Main
```

These commands compile the sources under `src/` and start the program in interactive mode. The required library must be obtained separately.

### Interactive Mode

Launch `Main` without arguments and choose an option:

```text
1 - Decomposição própria de uma matriz simétrica
2 - Cálculo da matriz de covariâncias
3 - Identificação de imagens
4 - Gerar novas imagens
5 - Sair
```

The bundled dataset is available in `ImageBase/`. When prompted for the image database folder, enter `ImageBase` if running from the repository root.

### Command-Line Mode

The application also accepts positional flag/value pairs. Examples (Linux / macOS classpath shown):

**Reconstruct images with 5 eigenfaces:**

```bash
java -cp "build:lib/commons-math3-3.6.1.jar" Main -f 2 -k 5 -d ImageBase
```

**Identify the closest image using 5 eigenfaces:**

```bash
java -cp "build:lib/commons-math3-3.6.1.jar" Main -f 3 -k 5 -i ImageBase/image_001.csv -d ImageBase
```

**Generate a new image using 5 components:**

```bash
java -cp "build:lib/commons-math3-3.6.1.jar" Main -f 4 -k 5 -d ImageBase
```

For Windows, replace the classpath separator `:` with `;`.

> **Important:** The current command-line parser reads arguments by their positions. Supply them in the order shown (`-f`, `-k`, then `-i` and/or `-d`). The implementation does not provide a general-purpose flag parser. The academic brief describes an additional text-output argument, but the supplied source does not implement that output option.

## Output Files

The application writes output files under the `lapr1-DIJ-06/` directory, creating subdirectories according to the selected operation.

| **Directory** | **Generated Output** |
| --- | --- |
| `Output/` | Reconstructed matrix (CSV) or generated image (JPG) |
| `Eigenfaces/` | Calculated eigenfaces (CSV and JPG) |
| `ImagensReconstruidas/` | Reconstructed images (CSV and JPG) |
| `Identificacao/` | Closest matching image (JPG) |

## Testing & Validation

The implementation includes numerical validation techniques and input checks:

- **Mean Absolute Error (MAE)** for matrix and image reconstruction.
- **Euclidean distance** for comparing projected images.
- Input validation for matrix dimensions and grayscale pixel intensity in the matrix-decomposition workflow.
- User-configurable component counts for studying the effect of dimensionality reduction.

**Automated test coverage is not documented:** the provided repository contains no identifiable unit-test suite, test runner configuration, or coverage report. These numerical metrics should not be confused with automated unit testing.

## Academic Context

| | |
| --- | --- |
| **Course** | LAPR1 — Laboratório/Projeto I |
| **Institution** | ISEP — Instituto Superior de Engenharia do Porto |
| **Degree** | B.Sc. in Computer Engineering |
| **Academic Year** | 2024/2025 |
| **Project Group** | DIJ-06 |
| **Project Type** | Academic group project |

## Notes

This project was developed for educational purposes to explore linear algebra techniques for grayscale image reconstruction and nearest-neighbor matching. Its image matching results should not be interpreted as verified identification of real individuals.

The code and documentation reflect the supplied academic project snapshot; the README does not claim production readiness, benchmarked accuracy, or automated test coverage.
