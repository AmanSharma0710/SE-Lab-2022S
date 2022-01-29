Python package to apply transformations to an image and see the results on prediction of an instance segmentation model.
Contains:
- 'InstanceSegmentationModel' - Class to generate predictions from an instance segmentation model.
- 'Dataset' - Class to generate a dataset from a .jsonl file.
- 'plot_visualization' - Function to plot the results of the predictions.
- 'FlipImage' - Class to flip an image.
- 'RotateImage' - Class to rotate an image.
- 'ResizeImage' - Class to resize an image.
- 'CropImage' - Class to crop an image.
- 'BlurImage' - Class to blur an image.

Required classes can be imported from the following modules:
- 'my_package.model' contains InstanceSegmentationModel
- 'my_package.data' contains Dataset
- 'my_package.analysis' contains plot_visualization
- 'my_package.data.transforms' contains FlipImage, RescaleImage, BlurImage, CropImage, RotateImage