package com.teamwizardry.wizardry.api.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

public class TreeNode<T> implements Iterable<T>
{
	private T data;
	private TreeNode<T> parent;
	private List<TreeNode<T>> children;
	
	public TreeNode(T data)
	{
		this.data = data;
		this.parent = null;
		this.children = new ArrayList<>();
	}
	
	/**
	 * Sets this TreeNode's data, then returns this
	 */
	public TreeNode<T> setData(T data)
	{
		this.data = data;
		return this;
	}
	
	/**
	 * Sets this TreeNode's parent, then returns this
	 */
	public TreeNode<T> setParent(TreeNode<T> parent)
	{
		this.parent = parent;
		return this;
	}
	
	/**
	 * Adds a child node to this TreeNode, setting the child's parent to this. Returns the child
	 */
	public TreeNode<T> addChild(TreeNode<T> child)
	{
		this.children.add(child.setParent(this));
		return child;
	}
	
	public T getData()
	{
		return data;
	}
	
	public TreeNode<T> getParent()
	{
		return parent;
	}
	
	public List<TreeNode<T>> getChildren()
	{
		return children;
	}

	@Override
	public Iterator<T> iterator()
	{
		return new TreeIterator<T>(this);
	}
	
	public Iterator<TreeNode<T>> nodeIterator()
	{
		return new TreeNodeIterator<TreeNode<T>>(this);
	}
	
	private class TreeIterator<U> implements Iterator<U>
	{
		public Queue<TreeNode<U>> queue = new LinkedList<>();
		
		public TreeIterator(TreeNode<U> root)
		{
			queue.add(root);
		}
		
		@Override
		public boolean hasNext()
		{
			return !queue.isEmpty();
		}
		
		@Override
		public U next()
		{
			if (hasNext())
			{
				TreeNode<U> node = queue.remove();
				queue.addAll(node.children);
				return node.data;
			}
			else throw new NoSuchElementException();
		}
	}
	
	private class TreeNodeIterator<U extends TreeNode<T>> implements Iterator<TreeNode<T>>
	{
		public Queue<TreeNode<T>> queue = new LinkedList<>();
		
		public TreeNodeIterator(TreeNode<T> root)
		{
			queue.add(root);
		}
		
		@Override
		public boolean hasNext()
		{
			return !queue.isEmpty();
		}
		
		@Override
		public TreeNode<T> next()
		{
			if (hasNext())
			{
				TreeNode<T> node = queue.remove();
				queue.addAll(node.children);
				return node;
			}
			else throw new NoSuchElementException();
		}
	}
}
