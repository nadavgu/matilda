from setuptools import setup, find_packages

setup(name='matilda',
      version='0.1.0',
      author='Nadav Gur',
      author_email='nadavgu@gmail.com',
      packages=find_packages(include='matilda*'),
      install_requires=[
            'maddie~=0.1.0',
            'protobuf~=4.23'
      ]
      )
