import setuptools

with open("README.md", "r", encoding="utf-8") as fh:
    long_description = fh.read()

setuptools.setup(
    name="my_package",
    version="0.0.1",
    author="Aman",
    author_email="sharmaaman2050as@gmail.com",
    description="Package to run transformations on a dataset before instance segmentation and detection",
    long_description=long_description,
    long_description_content_type="text",
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: OSI Approved :: MIT License",
        "Operating System :: OS Independent",
    ],
    package_dir={"": "src"},
    packages=setuptools.find_packages(where="src"),
    python_requires=">=3.6",
)